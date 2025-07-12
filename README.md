# API de Gestión de Franquicias

API REST para gestionar franquicias, sucursales y sus productos.

## Tecnologías

- Spring Boot
- WebFlux
- MySQL
- Docker

## Despliegue Rápido

### 1. Clonar y ejecutar

```bash
git clone https://github.com/yourusername/franchise-test-ms.git
cd franchise-test-ms
docker-compose up --build
```

Endpoints:
- **API**: http://localhost:8080
- **Base de datos**: http://localhost:8081 (phpMyAdmin)

### 2. Detener servicios

```bash
docker-compose down
```

## Endpoints
Los endpoints de ejemplo se encuentra en la carpeta postmanCollections, el cual contiene la colección necesaria apra realizar su consumo a tráves de postman o mediante curl desde la terminal.

Adicionalmente se generó un repositorio referente a la persisitencia de datos en terraform: https://github.com/owlbinary/franchise-test-infra.git, a modo de ejemplo de como se realizaría el aprovisionamiento de la misma desde terraform. No se aprovisionaron las herramientas utilizadas localmente, en específico Redis debido a que no se encuentra en el Free Tier de AWS.

Del mismo modo se agregan las variables necesarias para su conexión a la BD, mediante el servicio de parameter store, inyectando sus valores al contenedor como variables de entorno y así poder hacer su respectivo consumo desde el servicio. A efectos prácticos hay algunas credenciales en los archivos de configuración que se encuentran escritas explicitamente en el código.