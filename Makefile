.PHONY: help build docker-build docker-build-multiarch docker-push clean

IMAGE_ORG     := cnapcloud
IMAGE_NAME    := inicis-mock-server
IMAGE_VERSION := 1.0.0

IMAGE := $(IMAGE_ORG)/$(IMAGE_NAME):$(IMAGE_VERSION)

help: ## 사용 가능한 명령 목록 출력
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

build: ## Maven으로 JAR 빌드 (mvn clean package)
	mvn clean package

docker-build: build ## 로컬용 단일 플랫폼 Docker 이미지 빌드
	docker buildx build -f Dockerfile -t $(IMAGE) --load .

publish: build ## linux/amd64 + linux/arm64 멀티아키텍처 빌드 후 Docker Hub 푸시
	export BUILDX_NO_DEFAULT_ATTESTATIONS=1 && \
	export DOCKER_BUILDKIT=1 && \
	docker buildx build -f Dockerfile --platform linux/amd64,linux/arm64 -t $(IMAGE) --push .

clean: ## Docker buildx 캐시 정리
	docker buildx prune -f
