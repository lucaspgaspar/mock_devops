# Utiliza a imagem base do OpenJDK 17 (compatível com o Java 17 do projeto)
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR gerado pelo Maven para o diretório de trabalho no container
COPY target/teste-na-pratica_spring-0.0.1-SNAPSHOT.jar /app/app.jar

# Expõe a porta 8080 para conexões externas
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-XX:+UseContainerSupport", "-Xmx512m", "-jar", "/app/app.jar"]
