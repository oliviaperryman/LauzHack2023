
FROM python:3.8-slim-buster as dev
WORKDIR /src

COPY /src /src

RUN apt-get -y update

COPY requirements.txt requirements.txt
RUN pip3 install --upgrade pip setuptools wheel
RUN apt-get install -y python3-opencv
RUN pip3 install -r requirements.txt
