# EV6 유선 Android Auto TBT 테스트 v0.1.0

한국 내수형 EV6 표준형 5W `CVEV / KOR / 260902`와 HMG-API `1.0.2`를 대상으로 한 실차 검증용 빌드입니다. 진단 v0.1.0에서는 Android Auto 초기화가 성공했고 차량이 `IMAGE` 안내 형식을 제시했습니다. 이 빌드는 `ENUM` 형식으로 다음 회전 코드를 요청합니다. 안내 활성 상태에서 인식 가능한 회전과 그 회전의 거리 콜백이 들어오면 계기판에 표시합니다. HUD는 목록에 넣을 수 있는 회전 아이콘과 거리를 표시합니다.

지원되는 기본 이벤트는 직진·합류, 좌우회전, 유턴, 진출입로, 갈림길, 일부 회전교차로, 도착입니다. 재탐색·안내 종료·연결 해제 때 표시를 지웁니다. 거리 갱신이 8초 동안 없을 때도 표시가 지워집니다. 재탐색이나 갱신 중단 뒤에는 안내가 활성화된 상태에서 회전과 거리 콜백이 다시 들어오면 표시가 복구됩니다. USB 직결에서의 콜백 수신과 화면 표시, 순정 내비·HDA와의 동작은 실차에서 확인해야 합니다.

설치 ZIP은 [v0.1.0 사전 릴리즈](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/releases/tag/v0.1.0)에서 받을 수 있습니다.

## 설치와 첫 확인

1. 주차 상태에서 아래 경로에 ZIP을 압축 해제 없이 복사합니다.

   ```text
   USB:/HMGAPI/updates/EV6_KR_Wired_Android_Auto_TBT_v0.1.0_TEST.zip
   ```

2. 차량 데이터 USB 포트에 꽂고 HMG-API에서 **Check USB updates**를 누릅니다. **Mods → EV6 KOR Android Auto TBT (test)**를 켜고 **Apply changes**를 누릅니다. 적용 계획에서 재시작을 요청하면 화면 안내를 따릅니다.
3. 다른 Android Auto TBT 출력 모드가 켜져 있으면 테스트 중에는 끕니다. 순정 내비 길안내를 종료하고, 설치 USB와 캐빈프로 같은 어댑터를 빼고, 안드로이드폰을 차량 데이터 USB 포트에 직결합니다.
4. 차량 화면에서 **Android Auto**를 열고 티맵이나 네이버지도로 길안내를 시작합니다. 첫 회전과 거리가 계기판·HUD에 뜨는지 확인합니다. 안내를 종료한 뒤 회전 아이콘이나 로딩 표시가 남는지도 확인합니다. 이후 USB 케이블을 뺍니다.
5. **Mods → EV6 KOR Android Auto TBT (test) → Log**를 다시 열어 `AA_TBT_010` 줄을 캡처합니다. 사용한 내비 앱, USB 직결 여부, 계기판과 HUD 각각의 결과를 [Issues](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/issues)에 남겨주세요.

| 로그 | 의미 |
| --- | --- |
| `GPM_READY hooks=8/8` | 기존 7개 지점에 순정 내비 포커스 후크까지 등록됐습니다. |
| `AA_FORMAT from=IMAGE to=ENUM` | 차량이 제시하는 안내 형식을 회전 코드 형식으로 바꿨습니다. |
| `AA_NAV_INIT sent=ENUM result=true receiver=true` | GPM 초기화가 성공했고 이번 연결을 식별했습니다. |
| `AA_STATUS`, `AA_TURN`, `AA_DISTANCE` | 안내 상태, 회전 코드, 거리 콜백입니다. |
| `AA_DISPLAY_SENT`, `AA_DISPLAY_CLEARED` | 표시 API에 전송하거나 지우기를 요청했습니다. 실제 표시는 화면에서 확인합니다. |

Android Auto 연결이나 표시가 불안정하면 주차 상태에서 테스트 모드를 끄고 **Apply changes**를 누른 뒤 USB를 다시 연결합니다. `ENUM`으로 초기화했는데 `AA_TURN`과 `AA_DISTANCE`가 없거나, `GPM_HOOK_FAILED`가 보이면 해당 줄을 포함해 캡처해주세요.

## 소스 및 빌드

회전 아이콘 매핑과 표시 형식은 HMG-API(VysseCodes)의 `AaTurns`와 [Korea Projection 한국형 수정](https://github.com/StopDragon/HMG-API/blob/6b7211051f9d5edddf1b627fc92d71719bb6aa98/mods/wireless-carplay-aa/src/hmgapi/mods/wireless/AaTurns.java)을 참고했습니다. AGPL-3.0 라이선스를 따릅니다.

HMG-API 1.0.2의 컴파일된 `out/classes`, Android SDK 플랫폼 29 및 build-tools 35.0.0, JDK, Python과 로컬 서명 키가 필요합니다.

```sh
bash build.sh /path/to/HMG-API/out/classes /path/to/author.key
```
