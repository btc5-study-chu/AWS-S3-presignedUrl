
frontend-test:
# 	cd frontend && npm run lint
	cd frontend && npm run test

backend-test:
# 	cd backend && ./gradlew ktlintFormat && ./gradlew ktlintCheck
	cd backend && ./gradlew test

all:
	make backend-test
	make frontend-test
