FROM postgres:14.2-alpine
# run as non-root user but using user "postgres" instead
USER postgres
COPY revocation/V1.0.0__Create_DB.sql /docker-entrypoint-initdb.d/
