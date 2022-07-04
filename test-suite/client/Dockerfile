FROM node:18.3.0-alpine3.16
WORKDIR /app/client
COPY ./package.json /app/client
RUN npm install 
COPY . /app/client
CMD ["npm", "start"]