FROM node

WORKDIR /usr/src/transactions

COPY ./package.json ./package-lock.json /usr/src/transactions/


RUN npm install -g serve
RUN npm install
COPY . /usr/src/transactions/
RUN npm run build

CMD ["serve", "-s", "build"]
