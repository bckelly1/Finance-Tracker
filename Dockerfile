FROM maven:3.9.6

WORKDIR /usr/src/transactions

COPY ./pom.xml /usr/src/transactions/

RUN mvn dependency:copy-dependencies

COPY . /usr/src/transactions/
RUN mvn clean install
RUN mv /usr/src/transactions/target/*.jar /usr/src/transactions/application.jar

CMD ["java", "-jar", "/usr/src/transactions/application.jar"]

