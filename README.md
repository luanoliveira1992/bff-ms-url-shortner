# bff-ms-url-shortener
Simple url shortener with clojure and serverless on aws

## Using
- lein uberjar
- sls deploy

## Description
  We have two endpoints, the create and get. In create endpoint we pass a json body {url : 'http://myurl.com'}, and we return a shorter url by a randon token generated. This values we be save on dynamodb. Later, when call the returned url, we look for on dynamodb by the token, and return the related url with 302 htttp and location in header.

## How
- Serveless help us to create microservice on aws, is an IAAS;
- This clojure project use  uswitch clojure lib to make lambdas like;
- To connect with dynamoDb, where the token and the url will be; we use cognitect-labs;


## TODO
 - There are some things to do in code, to make it better, more idiomatic;
 - Make a serverless plugin to generate jar on pre-deploy
 - Tests :(
