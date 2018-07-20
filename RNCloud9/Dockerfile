FROM node:latest
RUN mkdir -p /code
WORKDIR /code

RUN apt-get update && apt-get install -y python-dev screen && cd /tmp/ && \
git clone https://github.com/facebook/watchman.git && cd watchman && ./autogen.sh && \
./configure && make -j4 && make install  && cd / && rm -rf /tmp/watchman

# RUN npm set progress=false && \
RUN npm install -g exp && npm install -g create-react-native-app && \
npm install -g awsmobile-cli && useradd -m -u 500 -s /bin/bash ec2-user

# to use this, assuming $PWD is the working directory of react-native code
# docker build -t node-exp .
# docker run -it --rm -p 80:80 -p 19000:19000 -p 19001:19001 -v "$PWD:/code" --user $UID  -e REACT_NATIVE_PACKAGER_HOSTNAME=`curl -s http://169.254.169.254/latest/meta-data/public-ipv4`  node-exp bash 