# Instrucciones para ejecutar la aplicación en Docker

1. Clonar el repositorio (si es necesario).
   git clone <url-del-repositorio>
   cd <nombre-del-repositorio>
   
2. Construir la aplicación.
    mvn clean package

3. Construir la imagen Docker.
  docker build -t nombre-imagen .

4. Ejecutar el contenedor.
   docker run -it --rm davidarielmarquez/ip-fraud-checker:latest


# Instrucciones de como usar la aplicación

- Para usar la aplicacion se usa el comando "traceip" sumado de un ip.
Por ejemplo: traceip 1.178.47.255

- Comando para detener Spring Shell
CTRL + P + Q 
