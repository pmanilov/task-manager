FROM bellsoft/liberica-openjdk-alpine:17.0.3.1
ARG USER=app
ARG UID=1001
ARG GID=1001
RUN addgroup --gid "$GID" "$USER" \
    && adduser \
    --disabled-password \
    --gecos "" \
    --home "$(pwd)" \
    --ingroup "$USER" \
    --no-create-home \
    --uid "$UID" \
    "$USER"
USER ${USER}
WORKDIR /app
COPY --chown=$USER:$USER build/libs/task-manager-1.0.jar /task-manager-1.0.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/task-manager-1.0.jar"]