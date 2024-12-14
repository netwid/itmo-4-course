#!/bin/bash

# Root CA
keytool -genkeypair -alias rootCA -keyalg RSA -keysize 2048 -validity 3650 -keystore rootCA.jks \
  -dname "CN=MyRootCA, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyState, C=MyCountry" -storepass changeit -keypass changeit
keytool -exportcert -alias rootCA -file rootCA.crt -keystore rootCA.jks -storepass changeit

# service1
SERVICE1_PATH=service1/src/main/resources
keytool -genkeypair -alias service1-1 -keyalg RSA -keysize 2048 -validity 365 -keystore ${SERVICE1_PATH}/service1.jks \
  -dname "CN=172.20.0.2, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyState, C=MyCountry" -storepass changeit -keypass changeit \
  -ext san=ip:172.20.0.2
keytool -certreq -alias service1-1 -file ${SERVICE1_PATH}/service1-1.csr -keystore ${SERVICE1_PATH}/service1.jks -storepass changeit \
  -ext san=ip:172.20.0.2
keytool -gencert -alias rootCA -infile ${SERVICE1_PATH}/service1-1.csr -outfile ${SERVICE1_PATH}/service1-1.crt \
  -keystore rootCA.jks -storepass changeit -validity 365 \
  -ext san=ip:172.20.0.2
keytool -genkeypair -alias service1-2 -keyalg RSA -keysize 2048 -validity 365 -keystore ${SERVICE1_PATH}/service1.jks \
  -dname "CN=172.20.0.12, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyState, C=MyCountry" -storepass changeit -keypass changeit \
  -ext san=ip:172.20.0.12
keytool -certreq -alias service1-2 -file ${SERVICE1_PATH}/service1-2.csr -keystore ${SERVICE1_PATH}/service1.jks -storepass changeit \
  -ext san=ip:172.20.0.12
keytool -gencert -alias rootCA -infile ${SERVICE1_PATH}/service1-2.csr -outfile ${SERVICE1_PATH}/service1-2.crt \
  -keystore rootCA.jks -storepass changeit -validity 365 \
  -ext san=ip:172.20.0.12
keytool -import -trustcacerts -alias rootCA -file rootCA.crt -keystore ${SERVICE1_PATH}/service1.jks -storepass changeit
keytool -import -trustcacerts -alias service1-1 -file ${SERVICE1_PATH}/service1-1.crt -keystore ${SERVICE1_PATH}/service1.jks -storepass changeit
keytool -import -trustcacerts -alias service1-2 -file ${SERVICE1_PATH}/service1-2.crt -keystore ${SERVICE1_PATH}/service1.jks -storepass changeit

# service2
SERVICE2_PATH=service2/src/main/resources
keytool -genkeypair -alias service2-1 -keyalg RSA -keysize 2048 -validity 365 -keystore ${SERVICE2_PATH}/service2.jks \
  -dname "CN=172.20.0.4, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyState, C=MyCountry" -storepass changeit -keypass changeit \
  -ext san=ip:172.20.0.4
keytool -certreq -alias service2-1 -file ${SERVICE2_PATH}/service2-1.csr -keystore ${SERVICE2_PATH}/service2.jks -storepass changeit \
  -ext san=ip:172.20.0.4
keytool -gencert -alias rootCA -infile ${SERVICE2_PATH}/service2-1.csr -outfile ${SERVICE2_PATH}/service2-1.crt \
  -keystore rootCA.jks -storepass changeit -validity 365 \
  -ext san=ip:172.20.0.4
keytool -genkeypair -alias service2-2 -keyalg RSA -keysize 2048 -validity 365 -keystore ${SERVICE2_PATH}/service2.jks \
  -dname "CN=172.20.0.5, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyState, C=MyCountry" -storepass changeit -keypass changeit \
  -ext san=ip:172.20.0.5
keytool -certreq -alias service2-2 -file ${SERVICE2_PATH}/service2-2.csr -keystore ${SERVICE2_PATH}/service2.jks -storepass changeit \
  -ext san=ip:172.20.0.5
keytool -gencert -alias rootCA -infile ${SERVICE2_PATH}/service2-2.csr -outfile ${SERVICE2_PATH}/service2-2.crt \
  -keystore rootCA.jks -storepass changeit -validity 365 \
  -ext san=ip:172.20.0.5
keytool -import -trustcacerts -alias rootCA -file rootCA.crt -keystore ${SERVICE2_PATH}/service2.jks -storepass changeit
keytool -import -trustcacerts -alias service2-1 -file ${SERVICE2_PATH}/service2-1.crt -keystore ${SERVICE2_PATH}/service2.jks -storepass changeit
keytool -import -trustcacerts -alias service2-2 -file ${SERVICE2_PATH}/service2-2.crt -keystore ${SERVICE2_PATH}/service2.jks -storepass changeit


#keytool -keysize 2048 -genkey -alias service1 -keyalg RSA -dname "CN=172.20.0.2,O=Myorganization,L=city,S=state,C=country" \
#  -keypass changeit -storepass changeit -keystore service1/src/main/resources/keystore.jks
#keytool -certreq -alias service1 -file service1/src/main/resources/service1.csr -keystore service1/src/main/resources/keystore.jks -storepass changeit
#keytool -import -trustcacerts -alias root -file (ROOT CERTIFICATE FILE NAME) -keystore domain.key