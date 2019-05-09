# Android client for Cognito Developer Federated Identities
Amazon Cognito supports developer authenticated identities, in
 addition to web identity federation through Facebook, Google, and Login with Amazon. With developer authenticated identities, you can register and authenticate users via your own existing authentication process, while still using Amazon Cognito to synchronize user data and access AWS resources. This is a working demonstration of an Android client for Cognito Developer Federated Identities. The basic mechanism is described in the official [blog](https://aws.amazon.com/cn/blogs/mobile/understanding-amazon-cognito-authentication-part-2-developer-authenticated-identities/).

The backend of this client is developed based on AWS serverless technologies, including Lambda, API Gateway and DynamoDb. Please refer to [Github](https://github.com/xfsnow/serverless/tree/master/cognitodev) for backend source code.
