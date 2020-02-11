# 키친포스

## 요구 사항

- 치킨집 운영을 위한 키친포스 시스템을 개발한다
- 상품
	- [ ]  고객이 먹게되는 음식의 최소 단위를 상품이라고 한다
	- [ ]  올바른 이름과 가격을 가져야 한다
	    - [ ]  이름은 1자 이상, 가격은 0원 이상이어야 한다
- 메뉴
	- [ ]  고객이 주문하는 최소 단위이다
	- [ ]  복수개의 상품을 포함할 수 있다
	- [ ]  포함된 상품은 각각 포함될 개수를 가진다
	- [ ]  올바른 이름과 가격을 가져야 한다
	    - [ ]  이름은 1자 이상, 가격은 0원 이상이어야 한다
	    - [ ]  메뉴의 가격 <= 상품 각각의 가격의 합
	- [ ]  반드시 하나의 메뉴 그룹에 속해야 한다
	- [ ]  메뉴를 조회할 수 있어야 한다
- 메뉴 그룹
	- [ ]  복수개의 메뉴를 포함할 수 있어야 한다
	- [ ]  메뉴 그룹을 조회할 수 있어야 한다
- 주문
	- [ ]  고객이 메뉴를 주문하게 되면 주문이 생성된다
	- [ ]  주문은 조리 중, 식사 중, 완료 3가지 상태를 가진다
	- [ ]  반드시 1개의 테이블과 연결되어야 한다
	- [ ]  주문은 올바른 메뉴와 가격을 포함해야 한다
	    - [ ]  존재하는 메뉴와 가격은 0 원 이상이어야 한다
	- [ ]  주문을 요청한 테이블이 기존에 존재해야 하고, 비어있는 상태가 아니여야 한다
	- [ ]  주문이 완료되면 조리 중 상태가 된다
- 테이블
	- [ ]  고객이 가게 내부에서 앉아서 식사를 하는 공간을 테이블이라고 한다
	- [ ]  테이블에 앉아있는 고객 수를 변경할 수 있다
	- [ ]  테이블의 상태는 비어있는 상태, 비어있지 않은 상태를 가질 수 있다
	- [ ]  고객이 앉으면 테이블을 비어있지 않은 상태로 만들어줘야 한다
	- [ ]  고객이 식사 중이라면 테이블 상태를 비어있는 상태로 변경할 수 있다
- 테이블 그룹
	- [ ]  여러개의 테이블을 포함할 수 있다
	- [ ]  테이블 그룹을 생성시에 올바른 테이블 정보를 포함해야 한다
	    - [ ]  테이블은 2개 이상이여야 하고, 비어있는 상태여야 한다
	- [ ]  테이블에서 발생한 식사가 종료되지 않았다면 테이블 그룹을 삭제할 수 없다

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
