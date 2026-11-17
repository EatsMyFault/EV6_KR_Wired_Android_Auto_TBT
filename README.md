# EV6 유선 Android Auto TBT 테스트 v0.1.3

한국 내수형 EV6 표준형 5W `CVEV / KOR / 260902`와 HMG-API `1.0.2` 이상을 대상으로 한 실차 검증용 빌드입니다. Android Auto에서 받은 다음 회전 화살표와 거리를 계기판과 지원되는 HUD에 표시합니다.

지원되는 기본 이벤트는 직진·합류, 좌우회전, 유턴, 진출입로, 갈림길, 일부 회전교차로, 도착입니다. 신호대기와 주행 중 표시 끊김이 개선됐는지, 순정 내비 및 HDA와 어떻게 동작하는지는 실차 확인이 필요합니다. [v0.1.3 테스트 릴리즈](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/releases/tag/v0.1.3)

## 설치 및 업데이트

처음 설치하거나 이전 버전에서 업데이트한다면 **[한국어 설치 가이드](docs/install-guide.md)**를 따라 주세요. 지원 차량 확인, Windows에서 USB 폴더 만들기, HMG-API에서 설치·적용하기, 유선 Android Auto 확인과 문제 제보 방법을 설명합니다.

1. [설치 ZIP](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/releases/download/v0.1.3/EV6_KR_Wired_Android_Auto_TBT_v0.1.3_TEST.zip)을 압축 해제 없이 `USB:/HMGAPI/updates/`에 복사합니다.
2. 주차 상태에서 차량 데이터 USB 포트에 꽂고 **HMG-API → Check USB updates → Install** 또는 **Update**를 누릅니다.
3. **Mods → EV6 KOR Android Auto TBT (test)**가 `0.1.3`이고 **On**인지 확인합니다. **Apply changes**를 누르고 재시작 안내가 나오면 따릅니다.
4. USB 메모리를 빼고 안드로이드폰을 같은 포트에 직결합니다. Android Auto에서 경로 안내를 시작해 계기판과 HUD를 확인합니다.

설치나 표시 문제가 있으면 [문제 해결](docs/install-guide.md#6-설치나-표시가-안-될-때)과 [문제 제보 양식](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/issues/new?template=bug-report.yml)을 확인해 주세요.

## v0.1.3 변경 사항

거리 입력이 15초 끊겨도 차량에서 Android Auto 길안내가 계속 활성이라고 확인되면 마지막으로 받은 회전과 거리를 약 2초마다 다시 보냅니다. 유지 시간은 마지막으로 받은 거리에 따라 15~60초입니다. 800m 이하에서는 기존 15초 제한이 적용되고, 2.5km에서는 마지막 갱신부터 최대 57.5초까지 유지합니다. 새 거리 입력이 들어오기 전까지 거리 숫자는 고정됩니다. 안내 종료, 재탐색, 순정 내비로 포커스 전환, 연결 해제 시 표시를 지웁니다.

같은 증상을 재현했다면 주차 후 `AA_TBT_013` 로그의 `AA_DISPLAY_HOLD`, `AA_DISPLAY_RESUMED`, `AA_DISPLAY_EXPIRED`를 확인해 주세요. [Open-HMG 무선 AA의 안내 상태 처리](https://github.com/Open-HMG/hmgapi-wireless-cp-aa/blob/main/wireless-carplay-aa/src/hmgapi/mods/wireless/AaTurns.java)와 [Supra iDrive6의 수신 공백 처리](https://github.com/Sq00ky/Supra-iDrive6-Android-Auto/blob/main/milestone-7/src/cluster/nav_core.cpp#L679-L730)를 참고했습니다.

## 소스 및 빌드

회전 아이콘 매핑과 표시 형식은 HMG-API(VysseCodes)의 `AaTurns`와 [Korea Projection 한국형 수정](https://github.com/StopDragon/HMG-API/blob/6b7211051f9d5edddf1b627fc92d71719bb6aa98/mods/wireless-carplay-aa/src/hmgapi/mods/wireless/AaTurns.java)을 참고했습니다. AGPL-3.0 라이선스를 따릅니다.

HMG-API 1.0.2의 컴파일된 `out/classes`, Android SDK 플랫폼 29 및 build-tools 35.0.0, JDK, Python과 로컬 서명 키가 필요합니다.

```sh
bash build.sh /path/to/HMG-API/out/classes /path/to/author.key
```
