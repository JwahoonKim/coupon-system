# 선착순 쿠폰 발급 구현해보기
## 요구사항
- **이벤트 기간 내**에 발급
- 선착순 이벤트는 **유저당 1번**의 쿠폰 발급
- 선착순 쿠폰의 **최대 쿠폰 발급 수량** 설정

## 진화시키기
- v1 : 동시성 문제가 발생하는 안일한 버전
- v2 : synchronized 키워드를 사용해 동시성 문제를 해결한 버전
- v3 : 분산락(redis)를 사용해 동시성 문제를 해결한 버전
- v4 : DB Lock을 사용해 동시성 문제를 해결한 버전
- v5 : 비동기 & redis lock을 사용한 버전
- v6 : 비동기 & redis script를 사용한 버전

## 시스템 아키텍처
<img width="582" alt="image" src="https://github.com/user-attachments/assets/7b5f43a7-c538-42f5-a227-d438fab15712">

## 새로 알게된 점
- 레디스 List를 큐로 사용해 비동기적으로 메시지를 처리하는 방법
- locust를 이용한 부하테스트 방법
- @Cacheable과 AopContext.currentProxy()를 활용한 로컬 + 리모트 캐시 갱신 방법 [(내부 호출에서도 AOP 적용되게끔 하기)](https://github.com/JwahoonKim/coupon-system/blob/3efed5c130e34adef03950837ccfbbae948c5d97/coupon-core/src/main/java/me/jahni/couponcore/service/CouponCacheService.java#L25)
- @Transcactional 안에서 락을 잡을때는 조심해야한다는 것
  1. tx 시작
  2. lock 획득
  3. logic 실행
  4. lock 반납
  5. tx 커밋
     
  이런 순서로 진행되면 1번 요청이 4~5번 사이 즉, 반납하고 커밋 직전일 때 2번 요청이 lock 흭득하고 logic을 실행하는 과정에서 동시성이 깨질 수 있다.
