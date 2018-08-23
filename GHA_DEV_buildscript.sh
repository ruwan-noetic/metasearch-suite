DEV_SERVER=dev.wearenoetic.com
BRANCH_NAME=origin/develop

rm -f -r ./google-hotel-ads-api/

echo -e "clone Universal Payment API \n"

git clone https://github.com/noeticlabs/google-hotel-ads-api.git

cd google-hotel-ads-api

git checkout $BRANCH_NAME 

git branch --set-upstream-to=origin/$BRANCH_NAME $BRANCH_NAME 

git pull

echo -e "Build google-hotel-ads-api \n"
mvn clean install -DskipTests

echo -e "Release google-hotel-ads-api \n"

mkdir ./external/target/gha-api

cp -R ./external/target/gha-api-*/* ./external/target/gha-api

echo -e "Backup gha-api on api .....\n"
scp -Cpvr3 root@$DEV_SERVER:/var/lib/tomcat8/webapps/gha-api  root@$DEV_SERVER:/var/lib/tomcat8/backup/gha-api_$(date +%Y%m%d_%H%M%S)

echo -e "Deploying gha-api on api .....\n"
scp -Cpvr ./external/target/gha-api root@$DEV_SERVER:/var/lib/tomcat8/webapps/

mkdir ./internal/target/gha-saas

cp -R ./internal/target/gha-saas-*/* ./internal/target/gha-saas

echo -e "Backup gha-saas on saas .....\n"
scp -Cpvr3 root@$DEV_SERVER:/var/lib/tomcat8/webapps/gha-saas  root@$DEV_SERVER:/var/lib/tomcat8/backup/gha-saas_$(date +%Y%m%d_%H%M%S)

echo -e "Deploying gha-saas on saas .....\n"
scp -Cpvr ./internal/target/gha-saas root@$DEV_SERVER:/var/lib/tomcat8/webapps/


