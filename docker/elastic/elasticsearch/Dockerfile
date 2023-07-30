ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
FROM docker.elastic.co/elasticsearch/elasticsearch:${ELASTIC_VERSION}

# Add your elasticsearch plugins setup here
# Example: RUN elasticsearch-plugin install analysis-icu
COPY plugin/900gle-plugin-8.8.1.zip plugin/900gle-plugin-8.8.1.zip
RUN elasticsearch-plugin install file:///usr/share/elasticsearch/plugin/900gle-plugin-8.8.1.zip