DIR := $(patsubst %/,%,$(dir $(abspath $(lastword $(MAKEFILE_LIST)))))

# The directory where mkdocs.yml lives
ROOT ?= $(DIR)/..
# The path to the Dockerfile
DOCKERFILE ?= ${DIR}/Dockerfile
# The path to the mkdocs_requirements.txt file, relative to ${ROOT}
REQUIREMENTS ?= docs/mkdocs_requirements.txt
# The port on which to serve the documentation on localhost
PORT ?= 8000

include $(DIR)/Makefile.inc
