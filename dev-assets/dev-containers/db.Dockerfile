FROM postgres:14.2-alpine
# run as non-root user
RUN addgroup -g 1001 -S user && adduser -u 1001 -S -s /bin/false -G user user
USER user
COPY revocation/V1.0.0__Create_DB.sql /docker-entrypoint-initdb.d/
