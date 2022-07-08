FROM node:18.3.0-alpine3.16
WORKDIR /app/server
COPY ./package.json /app/server
RUN npm install
COPY . /app/server
CMD ["npm", "start"]