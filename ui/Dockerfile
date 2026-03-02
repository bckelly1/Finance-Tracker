FROM node

COPY . /usr/src/transactions/

WORKDIR /usr/src/transactions

RUN npm install -g serve
RUN npm install
RUN npm run build

CMD ["serve", "-s", "build"]
