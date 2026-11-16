# EV6 유선 Android Auto TBT 테스트 v0.1.1

한국 내수형 EV6 표준형 5W `CVEV / KOR / 260902`와 HMG-API `1.0.2`를 대상으로 한 실차 검증용 빌드입니다. 진단 v0.1.0에서는 Android Auto 초기화가 성공했고 차량이 `IMAGE` 안내 형식을 제시했습니다. 이 빌드는 `ENUM` 형식으로 다음 회전 코드를 요청합니다. 안내 활성 상태에서 인식 가능한 회전과 그 회전의 거리 콜백이 들어오면 계기판에 표시합니다. HUD는 목록에 넣을 수 있는 회전 아이콘과 거리를 표시합니다.

지원되는 기본 이벤트는 직진·합류, 좌우회전, 유턴, 진출입로, 갈림길, 일부 회전교차로, 도착입니다. 같은 거리 콜백이 계속 들어오면 약 2초 간격으로 계기판과 지원되는 HUD 안내를 갱신합니다. 거리 콜백이 15초 동안 없으면 표시를 지웁니다. 이후 10초 이내에 같은 연결의 활성 안내에서 마지막 거리와 같거나 더 가까운 거리가 들어오면 표시를 복구합니다. 재탐색·안내 종료·연결 해제 또는 순정 내비로 포커스가 넘어가면 저장된 회전 정보도 지웁니다. USB 직결에서의 콜백 수신과 화면 표시, 순정 내비·HDA와의 동작은 실차에서 확인해야 합니다.

신호대기와 주행 중 표시 끊김이 개선됐는지는 실차 확인이 필요합니다. [v0.1.1 테스트 릴리즈](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/releases/tag/v0.1.1)

## 설치 및 업데이트

처음 설치하거나 v0.1.0에서 업데이트한다면 **[한국어 설치 가이드](docs/install-guide.md)**를 따라 주세요. 지원 차량 확인, Windows에서 USB 폴더 만들기, HMG-API에서 설치·적용하기, 유선 Android Auto 확인과 문제 제보 방법을 설명합니다.

1. [설치 ZIP](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/releases/download/v0.1.1/EV6_KR_Wired_Android_Auto_TBT_v0.1.1_TEST.zip)을 압축 해제 없이 `USB:/HMGAPI/updates/`에 복사합니다.
2. 주차 상태에서 차량 데이터 USB 포트에 꽂고 **HMG-API → Check USB updates → Install** 또는 **Update**를 누릅니다.
3. **Mods → EV6 KOR Android Auto TBT (test)**가 `0.1.1`이고 **On**인지 확인합니다. **Apply changes**를 누르고 재시작 안내가 나오면 따릅니다.
4. USB 메모리를 빼고 안드로이드폰을 같은 포트에 직결합니다. Android Auto에서 경로 안내를 시작해 계기판과 HUD를 확인합니다.

설치나 표시 문제가 있으면 [문제 해결](docs/install-guide.md#6-설치나-표시가-안-될-때)과 [문제 제보 양식](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/issues/new?template=bug-report.yml)을 확인해 주세요.

## 소스 및 빌드

회전 아이콘 매핑과 표시 형식은 HMG-API(VysseCodes)의 `AaTurns`와 [Korea Projection 한국형 수정](https://github.com/StopDragon/HMG-API/blob/6b7211051f9d5edddf1b627fc92d71719bb6aa98/mods/wireless-carplay-aa/src/hmgapi/mods/wireless/AaTurns.java)을 참고했습니다. AGPL-3.0 라이선스를 따릅니다.

HMG-API 1.0.2의 컴파일된 `out/classes`, Android SDK 플랫폼 29 및 build-tools 35.0.0, JDK, Python과 로컬 서명 키가 필요합니다.

```sh
bash build.sh /path/to/HMG-API/out/classes /path/to/author.key
```
