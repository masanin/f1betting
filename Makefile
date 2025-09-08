.PHONY: help build run stop logs clean shell

help:
	@echo "Commands:"
	@echo "  make build  - Build Docker image"
	@echo "  make run    - Run container"
	@echo "  make stop   - Stop container"
	@echo "  make logs   - View logs"
	@echo "  make clean  - Clean everything"
	@echo "  make shell  - Enter container shell"

build:
	docker-compose build --no-cache

run:
	docker-compose up -d

stop:
	docker-compose down

logs:
	docker-compose logs -f

clean:
	docker-compose down -v
	docker system prune -f
	./gradlew clean

shell:
	docker exec -it f1-betting-app sh

restart: stop build run logs