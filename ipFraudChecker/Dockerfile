# Usa una imagen base de Java (puedes cambiar la versión según sea necesario)
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR de tu aplicación al contenedor
COPY target/ip-fraud-checker-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto que tu aplicación va a usar
EXPOSE 8080

# Comando para ejecutar tu aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]