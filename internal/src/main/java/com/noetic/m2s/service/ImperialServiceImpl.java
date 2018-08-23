package com.noetic.m2s.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noetic.dto.HospitalityMeta;
import com.noetic.dto.hospitality.CancellationPolicyDTO;
import com.noetic.dto.hospitality.PackageDTO;
import com.noetic.dto.hospitality.PromotionDTO;
import com.noetic.dto.hospitality.RoomDTO;
import com.noetic.dto.hospitality.RoomPromotionPackageDTO;
import com.noetic.m2s.config.DeployProfile;
import com.noetic.m2s.config.DeployProfileConfig;
import com.noetic.m2s.config.DeployProfileConfigFactory;
import com.noetic.m2s.domain.external.PromotionDetail;
import com.noetic.m2s.domain.external.repository.PromotionDetailRepository;
import com.noetic.m2s.domain.internal.RunTimeConfig;
import com.noetic.m2s.enums.RunTimeTag;
import com.noetic.m2s.service.client.ImperialClient;
import com.noetic.m2s.util.MetaSerachUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by hurman on 02/10/2017.
 */
@Service
public class ImperialServiceImpl implements ImperialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImperialServiceImpl.class);
    private final ObjectMapper mapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    
 
    
    private static final String IMPERIAL_BASE_URL = "imperialBaseUrl";
    private static final String IMPERIAL_ENDPOINT_DOMAIN = "imperialEndpointDomain";
    private static final String MORTON_ENDPOINT_DOMAIN = "mortonEndpointDomain";
    
    @Autowired
    private ImperialClient imperialClient;
    
    @Autowired
    private PromotionDetailRepository promotionDetailRepository;
    
    @Autowired
    private RunTimeConfigService runTimeConfigService;


    @Override
    @Transactional
    @CachePut(value = "AvailabilityMetaCache")
    public HospitalityMeta fetchAvailabilityMetaEnriched() throws Exception {
        LOGGER.info("<== AvailabilityMeta Enriched ==>");
        return fetchAvailabilityMeta();
    }

    @Override
    @Transactional
    @Cacheable(value = "AvailabilityMetaCache")
    public HospitalityMeta fetchAvailabilityMetaCached() throws Exception {
        LOGGER.info("<== AvailabilityMeta Cached ==>");
        return fetchAvailabilityMeta();
    }


    private HospitalityMeta fetchAvailabilityMeta() throws Exception {
        List<PromotionDetail> promotionDetailList = promotionDetailRepository.findAll();
        LOGGER.debug(" promotionDetailList size {} :", promotionDetailList.size());

        ArrayList<PromotionDTO> promotionDTOs = fetchPromotionInfo();
        LOGGER.debug(" PromotionDTO size {} :", promotionDTOs.size());

        RoomPromotionPackageDTO roomPromotionPackageDTO =
                fetchRoomPromotionsAndPackages();
        LOGGER.debug(" roomPromotionPackageDTOS size {} :", roomPromotionPackageDTO);

        PackageDTO packageDTO = fetchPackageInfo();
        LOGGER.debug(" packageDTOS size {} :", packageDTO);

        ArrayList<RoomDTO> roomDTOS = fetchRoomInfo();
        LOGGER.debug(" roomDTOS size {} :", roomDTOS);

        return generateHospitalityMeta(
                promotionDetailList,
                promotionDTOs,
                packageDTO,
                roomDTOS,
                roomPromotionPackageDTO);

    }

    private HospitalityMeta generateHospitalityMeta(List<PromotionDetail> promotionDetailList,
            ArrayList<PromotionDTO> promotionDTOList, PackageDTO packageDTO, ArrayList<RoomDTO> roomDTOList,
            RoomPromotionPackageDTO roomPromotionPackageDTO) throws ParseException {

        HospitalityMeta hospitalityMeta = new HospitalityMeta();

        ArrayList<HospitalityMeta.PromotionMeta> promotionMetas = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOList) {
            HospitalityMeta.PromotionMeta promotionMeta = new HospitalityMeta.PromotionMeta();

            promotionMeta.setPromotionCode(promotionDTO.getCode());
            promotionMeta.setNames(promotionDTO.getNames());
            promotionMeta.setDescriptions(promotionDTO.getDescriptions());

            promotionMetas.add(promotionMeta);
        }

        // match active promotions against DB promotion and get more info
        for (PromotionDetail promotionDetail : promotionDetailList) {
            for (HospitalityMeta.PromotionMeta promotionMeta : promotionMetas) {
                //compare against the promotion code 
                if (promotionMeta.getPromotionCode().equals(promotionDetail.getPromotionCode())) {
                    promotionMeta.setPromotionName(promotionDetail.getPromotionName());
                    promotionMeta.setRefundable(promotionDetail.isRefundable());
                    promotionMeta.setStartDate(promotionDetail.getStartDate());
                    promotionMeta.setEndDate(promotionDetail.getEndDate());
                   promotionMeta.setActive(promotionDetail.isActive());
                }
            }
        }

            hospitalityMeta.setPromotionMetaList(promotionMetas);
        // set active promotions after filtering it

        LOGGER.debug("hospitalityMeta size {} :", hospitalityMeta);

        // populate packages
        LinkedHashMap<String, PackageDTO.Package> parkingHashMap = new LinkedHashMap<>();
        for (PackageDTO.Package package1 : packageDTO.getPackages()) {
            if (package1.getPackageName().toLowerCase().trim().contains("parking")) {
                parkingHashMap.put(package1.getUrn(), package1);
            }
        }
        LOGGER.debug(" parkingHashMap size {} :", parkingHashMap.size());

        LinkedHashMap<String, PackageDTO.Package> breakfastHashMap = new LinkedHashMap<>();
        for (PackageDTO.Package package1 : packageDTO.getPackages()) {
            if (package1.getPackageName().toLowerCase().contains("breakfast")) {
                breakfastHashMap.put(package1.getUrn(), package1);
            }
        }
        LOGGER.debug(" breakfastHashMap size {} :", breakfastHashMap.size());

        ArrayList<HospitalityMeta.PackageMeta> packageMetas = new ArrayList<>();
        for (PackageDTO.Package package1 : packageDTO.getPackages()) {

            HospitalityMeta.PackageMeta packageMeta = new HospitalityMeta.PackageMeta();

            packageMeta.setUrn(package1.getUrn());
            packageMeta.setPackageName(package1.getPackageName());
            packageMeta.setPackageCode(package1.getPackageCode());
            packageMeta.setContainsParking(parkingHashMap.containsKey(package1.getUrn()) ? true : false);
            packageMeta.setContainsBreakfast(breakfastHashMap.containsKey(package1.getUrn()) ? true : false);
            packageMetas.add(packageMeta);
        }

        // set packages
        hospitalityMeta.setPackageMetaList(packageMetas);
        LOGGER.debug(" hospitalityMeta size {} :", hospitalityMeta);

        // populate rooms
        ArrayList<HospitalityMeta.RoomMeta> roomMetas = new ArrayList<>();
        for (RoomDTO roomDTO : roomDTOList) {

            HospitalityMeta.RoomMeta roomMeta = new HospitalityMeta.RoomMeta();

            roomMeta.setUrn(roomDTO.getUrn());
            roomMeta.setRoomCode(roomDTO.getRoomCode());
            roomMeta.setHotel(roomDTO.getHotel());
            roomMeta.setCaptions(roomDTO.getCaptions());
            roomMeta.setNames(roomDTO.getNames());
            roomMeta.setDescriptions(roomDTO.getDescriptions());
            roomMeta.setPhotoUrl(roomDTO.getPhotoUrl());

            roomMetas.add(roomMeta);
        }

        // set rooms
        hospitalityMeta.setRoomMetaList(roomMetas);
        LOGGER.debug(" hospitalityMeta size {} :", hospitalityMeta);

        // so if a room promotion contains package which have breakfast/parking then it has parking/breakfast
        // need to filter these to only contain active promotions

        ArrayList<HospitalityMeta.RoomPromotionMeta> roomPromotionMetas = new ArrayList<>();
        for (RoomPromotionPackageDTO.RoomPromotion roomPromotion : roomPromotionPackageDTO.getRoomPromotions()) {

            HospitalityMeta.RoomPromotionMeta roomPromotionMeta = new HospitalityMeta.RoomPromotionMeta();

            // populate room info
            for (HospitalityMeta.RoomMeta roomMeta : roomMetas) {

                if (roomMeta.getUrn().equals(roomPromotion.getRoomDetailUrn())) {
                    roomPromotionMeta.setRoomUrn(roomMeta.getUrn());
                    roomPromotionMeta.setRoomCode(roomMeta.getRoomCode());
                    roomPromotionMeta.setHotelCode(roomMeta.getHotel());
                }
            }

            // populate promotion info
            for (HospitalityMeta.PromotionMeta promotionMeta : promotionMetas) {
                roomPromotionMeta.setPromotionCode(promotionMeta.getPromotionCode());
            }

            // remove duplicate packages
            List<String> uniquePackageList = new ArrayList<>(new LinkedHashSet<>(roomPromotion.getPackages()));
            LOGGER.debug(" uniquePackageList size {} :", uniquePackageList);
            // find package meta and set them
            List<HospitalityMeta.PackageMeta> packageMetaList = new ArrayList<>();
            for (String packageUrn : uniquePackageList) {
                for (HospitalityMeta.PackageMeta packageMeta : packageMetas) {
                    if (packageUrn.equals(packageMeta.getUrn())) {
                        packageMetaList.add(packageMeta);
                    }
                }
            }

            // populate packages
            roomPromotionMeta.setPackages(packageMetaList);
            roomPromotionMetas.add(roomPromotionMeta);
        }

        LOGGER.debug(" hospitalityMeta size {} :", hospitalityMeta);

        for (Iterator<HospitalityMeta.RoomPromotionMeta> iterator = roomPromotionMetas.iterator();
             iterator.hasNext(); ) {
            HospitalityMeta.RoomPromotionMeta roomPromotionMeta = iterator.next();
            if (MetaSerachUtil.isNullOrWhiteSpace(roomPromotionMeta.getPromotionUrn())
                    && MetaSerachUtil.isNullOrWhiteSpace(roomPromotionMeta.getPromotionCode()) ||
                    MetaSerachUtil.isNullOrWhiteSpace(roomPromotionMeta.getRoomUrn()) &&
                            MetaSerachUtil.isNullOrWhiteSpace(roomPromotionMeta.getRoomCode())) {
                iterator.remove();
            }
        }

        // filter through packages and set parking/breakfast to true
        for (HospitalityMeta.RoomPromotionMeta roomPromotionMeta : roomPromotionMetas) {
            boolean isBrk = false;
            boolean isPrk = false;
            for (HospitalityMeta.PackageMeta packageMeta : roomPromotionMeta.getPackages()) {
                if (!isBrk) {
                    if (packageMeta.isContainsBreakfast()) {
                        roomPromotionMeta.setContainsBreakfast(true);
                        isBrk = true;
                    } else {
                        roomPromotionMeta.setContainsBreakfast(false);
                    }
                }

                if (!isPrk) {
                    if (packageMeta.isContainsParking()) {
                        roomPromotionMeta.setContainsParking(true);
                        isPrk = true;
                    } else {
                        roomPromotionMeta.setContainsParking(false);
                    }
                }

                if (isPrk && isBrk) {
                    break;
                }
            }
        }

        hospitalityMeta.setRoomPromotionMetaList(roomPromotionMetas);
        LOGGER.debug(" hospitalityMeta size {} :", hospitalityMeta);

        return hospitalityMeta;
    }

    @Override
    public ArrayList<RoomDTO> fetchRoomInfo() throws Exception {

        ArrayList<RoomDTO> allRooms = new ArrayList<>();
        DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
        //get deployConfig tag configs
        Map<String, RunTimeConfig> configs = runTimeConfigService
                .getRuntimeConfigCached(deployProfileConfig.getDeployProfile(), RunTimeTag.DEPLOY_CONFIG);
        
        String stringRequest = configs.get(IMPERIAL_ENDPOINT_DOMAIN).getConfigValue()+ "/en/api/rooms/descriptions";
        String response = imperialClient.doGet(stringRequest, true);
        ArrayList<RoomDTO> imperialRooms = mapper.readValue(response, new TypeReference<ArrayList<RoomDTO>>() {
        });
        LOGGER.debug("imperialRooms size {} :", imperialRooms.size());
        
        String stringRequest1 = configs.get(MORTON_ENDPOINT_DOMAIN).getConfigValue() + "/en/api/rooms/descriptions";
        String response1 = imperialClient.doGet(stringRequest1, true);
        ArrayList<RoomDTO> mortonRooms = mapper.readValue(response1, new TypeReference<ArrayList<RoomDTO>>() {
        });
        LOGGER.debug("mortonRooms size {} :", mortonRooms.size());

        allRooms.addAll(imperialRooms);
        allRooms.addAll(mortonRooms);
        LOGGER.debug("allRooms size {} :", allRooms.size());

        return allRooms;
    }

    @Override
    public ArrayList<PromotionDTO> fetchPromotionInfo() throws Exception {

        ArrayList<PromotionDTO> allPromotions = new ArrayList<>();
        DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
        Map<String, RunTimeConfig> configs = runTimeConfigService
                .getRuntimeConfigCached(deployProfileConfig.getDeployProfile(), RunTimeTag.DEPLOY_CONFIG);      
                
        String stringRequest = configs.get(IMPERIAL_ENDPOINT_DOMAIN).getConfigValue()
                + "/en/api/promotions/descriptions";
        String response = imperialClient.doGet(stringRequest, true);
        ArrayList<PromotionDTO> imperialPromotions = mapper.readValue(response,
                new TypeReference<ArrayList<PromotionDTO>>() {
                });
        LOGGER.debug("imperialPromotions size {} :", imperialPromotions.size());
        String stringRequest1 = configs.get(MORTON_ENDPOINT_DOMAIN).getConfigValue()
                + "/en/api/promotions/descriptions";
        String response1 = imperialClient.doGet(stringRequest1, true);
        ArrayList<PromotionDTO> mortonPromotions = mapper.readValue(response1,
                new TypeReference<ArrayList<PromotionDTO>>() {
                });
        LOGGER.debug("mortonPromotions size {} :", mortonPromotions.size());

        allPromotions.addAll(imperialPromotions);
        allPromotions.addAll(mortonPromotions);

        allPromotions = new ArrayList<PromotionDTO>(new LinkedHashSet<PromotionDTO>(allPromotions));

        LOGGER.debug("allPromotions size {} :", allPromotions.size());

        return allPromotions;
    }

    @Override
    public CancellationPolicyDTO fetchCancellationPolicy() throws Exception {

        DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
        RunTimeConfig config = runTimeConfigService.getRunTimeConfigByConfigKeyAndEnvironmentCached(
                deployProfileConfig.getDeployProfile(), IMPERIAL_BASE_URL);
        String stringRequest = config.getConfigValue() + "admin/cancellation-policy/";
        String response = imperialClient.doGet(stringRequest, false);
        return mapper.readValue(response, CancellationPolicyDTO.class);
    }

    @Override
    public RoomPromotionPackageDTO fetchRoomPromotionsAndPackages() throws Exception {

        RoomPromotionPackageDTO roomPromotionPackageDTO = new RoomPromotionPackageDTO();
        DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
        String stringRequest = "";
        if (deployProfileConfig.getDeployProfile() == DeployProfile.LOCAL) {
            // hit DEV if we are running locally
            RunTimeConfig config = runTimeConfigService
                    .getRunTimeConfigByConfigKeyAndEnvironmentCached(DeployProfile.DEV, IMPERIAL_BASE_URL);
            stringRequest = config.getConfigValue() + "admin/room-promotion";
        } else {
            RunTimeConfig config = runTimeConfigService.getRunTimeConfigByConfigKeyAndEnvironmentCached(
                    deployProfileConfig.getDeployProfile(), IMPERIAL_BASE_URL);
            stringRequest = config.getConfigValue() + "admin/room-promotion";
        }
        String response = imperialClient.doGet(stringRequest, false);
        roomPromotionPackageDTO = mapper.readValue(response, RoomPromotionPackageDTO.class);

        // we need to get RoomPromotion variations
        ArrayList<RoomPromotionPackageDTO> roomPromotionPackageDTOS = new ArrayList<>();
        for (RoomPromotionPackageDTO.RoomPromotion roomPromotion : roomPromotionPackageDTO.getRoomPromotions()) {

            if (deployProfileConfig.getDeployProfile() == DeployProfile.LOCAL) {
                // hit DEV if we are running locally
                RunTimeConfig config = runTimeConfigService
                        .getRunTimeConfigByConfigKeyAndEnvironmentCached(DeployProfile.DEV, IMPERIAL_BASE_URL);
                stringRequest = config.getConfigValue() + "admin/room-promotion/" +
                        roomPromotion.getPromotionDetailUrn();
            } else {
                RunTimeConfig config = runTimeConfigService.getRunTimeConfigByConfigKeyAndEnvironmentCached(
                        deployProfileConfig.getDeployProfile(), IMPERIAL_BASE_URL);
                stringRequest = config.getConfigValue() + "admin/room-promotion/" +
                        roomPromotion.getPromotionDetailUrn();
            }
            String response1 = imperialClient.doGet(stringRequest, false);
            RoomPromotionPackageDTO temp = mapper.readValue(response1, RoomPromotionPackageDTO.class);
            roomPromotionPackageDTOS.add(temp);
        }

        roomPromotionPackageDTOS.add(roomPromotionPackageDTO);
        LOGGER.debug("roomPromotionPackageDTOS size {} :", roomPromotionPackageDTOS);

        RoomPromotionPackageDTO returnVal = new RoomPromotionPackageDTO();
        List<RoomPromotionPackageDTO.RoomPromotion> finalList = new ArrayList<>();
        for (RoomPromotionPackageDTO roomPromotionPackageDTO1 : roomPromotionPackageDTOS) {
            finalList.addAll(roomPromotionPackageDTO1.getRoomPromotions());
        }
        returnVal.setRoomPromotions(finalList);

        LOGGER.debug("returnVal size {} :", returnVal);

        return returnVal;
    }

    @Override
    public PackageDTO fetchPackageInfo() throws Exception {
        DeployProfileConfig deployProfileConfig = DeployProfileConfigFactory.forEnvironment();
        // ImperialBaseUrl
        RunTimeConfig config = runTimeConfigService.getRunTimeConfigByConfigKeyAndEnvironmentCached(
                deployProfileConfig.getDeployProfile(), IMPERIAL_BASE_URL);
        String stringRequest = config.getConfigValue() + "admin/package";
        String response = imperialClient.doGet(stringRequest, false);
        return mapper.readValue(response, new TypeReference<PackageDTO>() {
        });
    }
    
    /**
     * Filter the valid ones
     * @param promoList
     * @return Filtered PromotionMeta
     * @throws ParseException
     */
    public List<HospitalityMeta.PromotionMeta> getValidPromotions(List<HospitalityMeta.PromotionMeta> promoList,
            String checkIndDate, int numOfNights) throws ParseException {

        List<HospitalityMeta.PromotionMeta> validPromotionMetas = new ArrayList<>();
        // looping through the raw promotion list to filter
        for (HospitalityMeta.PromotionMeta promotionMeta : promoList) {

            if (promotionMeta.isActive()) {

                // promotion start & end dates are null means they are active indefinitely
                if ((promotionMeta.getStartDate() == null) && (promotionMeta.getEndDate() == null)) {
                    validPromotionMetas.add(promotionMeta);
                } else {
                    // get check in date from the query message
                    long checkIn = MetaSerachUtil.convertShortDateToEpoch(checkIndDate);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(checkIn);
                    // get the checkout by adding the stays
                    c.add(Calendar.DATE, numOfNights);
                    long checkout = c.getTimeInMillis();
                    // get dates without time
                    Date promoStartDate = MetaSerachUtil.getDateWithoutTime(new Date(promotionMeta.getStartDate()));
                    Date promoEndDate = MetaSerachUtil.getDateWithoutTime(new Date(promotionMeta.getEndDate()));

                    // check whether checkIn & checkOut are lies between promotion start & end dates
                    if (MetaSerachUtil.checkDateIsInDateRange(promoStartDate, promoEndDate, new Date(checkIn))
                            && MetaSerachUtil.checkDateIsInDateRange(promoStartDate, promoEndDate,
                                    new Date(checkout))) {
                        validPromotionMetas.add(promotionMeta);
                    }
                }

            }
        }
        return validPromotionMetas;
    }


}
