FROM python:alpine3.15

ARG PORT=8000
ARG REQUIREMENTS=mkdocs_requirements.txt

RUN apk upgrade --update-cache -a \
 && apk add --no-cache \
      git \
      git-fast-import \
      openssh \
 && apk add --no-cache --virtual .build gcc musl-dev

COPY ${REQUIREMENTS} requirements.txt
RUN pip install --no-cache-dir -r requirements.txt \
 && apk del .build gcc musl-dev \
 && rm -rf /tmp/* /root/.cache \
 && find ${PACKAGES} \
      -type f \
      -path "*/__pycache__/*" \
      -exec rm -f {} \;

WORKDIR /docs
EXPOSE ${PORT}
ENTRYPOINT ["mkdocs"]
CMD ["serve", "--dev-addr=0.0.0.0:${PORT}"]
