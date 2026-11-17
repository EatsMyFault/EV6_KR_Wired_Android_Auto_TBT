# EV6 유선 Android Auto TBT 설치 가이드

HMG-API가 이미 설치된 한국 내수형 EV6에 **EV6 KOR Android Auto TBT (test) v0.1.3**을 추가하거나 이전 버전을 업데이트하는 방법입니다. TBT는 계기판과 지원되는 HUD에 표시되는 회전 화살표와 다음 안내 지점까지의 거리입니다.

> 주차 상태에서 설치와 설정을 진행하세요. 이 모드는 실차 검증용 테스트 버전입니다. 설치 또는 사용으로 발생한 오류, 데이터 손실, 장치 손상에 대해 제작자는 책임지지 않습니다.

## 1. 차량과 준비물 확인

| 확인 항목 | 조건 |
| --- | --- |
| 차량 | 한국 내수형 EV6, 표준형 5W(Gen5W) |
| 차량·지역·펌웨어 | `CVEV / KOR / 260902` |
| HMG-API | `1.0.2` 이상. 빌드 및 호환성 확인의 기준은 `1.0.2`입니다. |
| HMG-API 상태 | 앱 상단에 **Hooks on (map checked)** 표시 |
| 설치 준비물 | 차량에서 인식하는 USB 메모리, USB에 파일을 복사할 PC. 아래 예시는 Windows 기준입니다. |
| 길안내 확인 | 순정 유선 Android Auto가 작동하는 안드로이드폰과 데이터 전송이 가능한 USB 케이블 |

차량 앱 목록에서 **HMG-API**를 열어 버전과 차량 정보를 확인합니다. 차량 코드·지역·펌웨어가 다르거나 호환되지 않는다는 경고가 나오면 설치를 멈추고 지원 조건을 확인하세요. 폰을 차량 데이터 포트에 직접 연결했을 때 기존 Android Auto가 먼저 정상적으로 열려야 합니다.

## 2. 설치 ZIP 받기

v0.1.3 테스트 릴리즈 화면에서 **Assets**를 열고 `EV6_KR_Wired_Android_Auto_TBT_v0.1.3_TEST.zip`을 받습니다. 파일명이 같은 설치 에셋을 선택하세요. **Source code (zip)**은 개발용 소스입니다. [v0.1.3 테스트 릴리즈](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/releases/tag/v0.1.3) · [설치 ZIP 다운로드](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/releases/download/v0.1.3/EV6_KR_Wired_Android_Auto_TBT_v0.1.3_TEST.zip)

## 3. USB에 복사하기

1. PC의 파일 탐색기에서 USB 드라이브를 엽니다. `F:`는 예시 드라이브 문자입니다.
2. USB를 열었을 때 처음 보이는 위치에 `HMGAPI` 폴더를 만들고, 그 안에 `updates` 폴더를 만듭니다. 기존 폴더가 있으면 그대로 사용하세요.
3. `updates` 폴더에 **압축을 풀지 않은 ZIP 파일**을 복사합니다. 이전 AA TBT 설치 ZIP이 이 폴더에 있다면 혼동을 피하도록 PC의 다른 폴더에 옮겨 두세요.
4. 아래처럼 보이는지 확인하고 USB를 안전하게 꺼냅니다.

```text
F:\
└─ HMGAPI
   └─ updates
      └─ EV6_KR_Wired_Android_Auto_TBT_v0.1.3_TEST.zip
```

## 4. 차량에서 설치하고 적용하기

1. 차량을 주차하고 인포테인먼트가 켜지면 평소 유선 Android Auto를 쓰는 **데이터 USB 포트**에 USB 메모리를 꽂습니다.
2. **HMG-API → Check USB updates**를 누릅니다. 목록에서 **EV6 KOR Android Auto TBT (test) 0.1.3**을 확인합니다.
3. 처음 설치한다면 **Install**, 이 모드의 이전 버전이 설치돼 있다면 **Update**를 누릅니다. 이전 버전 위에 바로 설치할 수 있습니다. 처음 설치할 때 작성자 확인 창이 뜨면 다운로드한 저장소와 모드 이름을 확인한 뒤 **Install**을 누릅니다.
4. 설치 완료 메시지가 나오면 메인 화면으로 돌아갑니다. **Mods → EV6 KOR Android Auto TBT (test)**를 열어 버전이 `0.1.3`인지 확인하고 **On**으로 설정합니다.
5. 메인 화면에서 **Apply changes**를 누릅니다. **Apply these changes?** 창에서 적용 계획을 확인하고 **Apply**를 누릅니다. **Applied: restart to finish**가 나오면 **Restart now**를 누르고 인포테인먼트가 다시 켜질 때까지 기다립니다. 재시작을 미뤘다면 메인 화면의 **Restart unit**에서 실행합니다.
6. **Mods**에서 모드가 `0.1.3 · On`으로 표시되는지 확인한 뒤 USB 메모리를 뺍니다. [HMG-API 모드 설치·업데이트 안내](https://github.com/Open-HMG/HMG-API/wiki/Installing-and-removing-mods)

## 5. 유선 Android Auto에서 확인하기

1. 처음 확인할 때는 순정 내비의 경로 안내를 종료합니다. 다른 Android Auto TBT 출력 모드가 켜져 있으면 **Off**로 바꾸고 **Apply changes**를 적용합니다.
2. 캐빈프로 등 어댑터를 빼고, 안드로이드폰을 차량 데이터 포트에 케이블로 직접 연결합니다. 차량 화면에서 **Android Auto**를 엽니다.
3. 네이버지도나 티맵에서 목적지를 지정하고 **길안내를 시작**합니다. 계기판의 내비 안내 화면을 열어 다음 회전 화살표와 거리가 표시되는지 확인합니다. HUD가 있는 차량은 HUD도 따로 확인합니다.
4. 신호대기 때 표시가 유지되는지, 출발하거나 재탐색했을 때 다음 회전과 거리가 갱신되는지 확인합니다. 안내를 끝낸 뒤 이전 화살표나 로딩 표시가 남는지도 확인해 주세요. 화면 조작과 로그 확인은 주차 후 진행합니다.

## 6. 설치나 표시가 안 될 때

| 증상 | 확인할 내용 |
| --- | --- |
| USB 업데이트에 ZIP이 안 보임 | USB의 첫 화면에 `HMGAPI`가 있는지, 그 안의 `updates` 폴더에 설치 ZIP이 들어 있는지 확인합니다. 데이터 포트에 꽂고 **Check USB updates**를 다시 실행합니다. 다른 USB 메모리도 시도할 수 있습니다. |
| 설치가 거부되거나 적용 상태가 오류임 | HMG-API 버전, **Hooks on (map checked)** 상태, `CVEV / KOR / 260902` 지원 조건을 확인합니다. **Apply changes**의 오류 또는 재시작 메시지를 기록해 주세요. |
| Android Auto 화면은 뜨지만 TBT가 안 나옴 | 모드 `0.1.3 · On`, 적용·재시작 완료 여부, 폰 직결과 현재 길안내 상태를 확인합니다. 아래 로그에서 초기화와 회전·거리 수신을 확인합니다. |
| 신호대기 중 사라지거나 계기판과 HUD 표시가 다름 | 주차 후 문제가 난 시각의 로그를 확인합니다. 정차 후 몇 초에 사라졌는지, 출발 후 언제 돌아왔는지, 계기판과 HUD 중 어느 쪽이 사라졌는지를 함께 기록합니다. |

**HMG-API → Mods → EV6 KOR Android Auto TBT (test) → Log**에서 `AA_TBT_013`으로 시작하는 줄을 찾습니다. 모드 로그 화면에 **Save log to USB** 버튼이 보이면 로그를 파일로 저장할 수 있습니다. 버튼이 없으면 문제 시각 전후를 사진으로 남겨도 됩니다.

| 로그 | 의미 |
| --- | --- |
| `GPM_READY hooks=8/8` | 필요한 Android Auto 관찰·표시용 후크가 등록됐습니다. |
| `AA_NAV_INIT sent=ENUM result=true receiver=true` | Android Auto가 회전 코드 형식으로 초기화됐습니다. |
| `AA_STATUS`, `AA_TURN`, `AA_DISTANCE` | 안내 상태, 회전, 거리 정보 수신입니다. 회전과 거리 로그는 일부만 기록합니다. |
| `AA_DISPLAY_HOLD` | 거리 갱신이 15초 이상 끊겼지만 Android Auto가 안내 중이라 마지막 회전과 거리를 잠시 다시 보냅니다. |
| `AA_DISPLAY_RESUMED` | 다시 보내는 중 새 거리 정보가 들어왔습니다. |
| `AA_DISPLAY_EXPIRED`, `AA_EXPIRED_GUIDANCE` | 표시가 만료됐습니다. 마지막 정상 갱신 이후 시간, 유지 한도, Android Auto 안내 상태를 확인할 수 있습니다. |
| `AA_DISPLAY_SENT`, `AA_DISPLAY_CLEARED` | 표시를 보내거나 지우도록 요청했습니다. 계기판·HUD에 실제 반영됐는지는 화면에서 확인합니다. |
| `GPM_HOOK_FAILED` | 필요한 후크 등록에 실패했습니다. 관련 줄을 포함해 주세요. |

제보할 때는 차량 펌웨어, HMG-API 및 모드 버전, 내비 앱 이름, USB 직결 여부, 증상과 발생 시각을 적어 주세요. 로그나 사진을 공개하기 전에 VIN, 기기 식별자, 목적지 등 개인정보를 가려 주세요. [GitHub 문제 제보 양식](https://github.com/EatsMyFault/EV6_KR_Wired_Android_Auto_TBT/issues/new?template=bug-report.yml)

## 7. 모드 끄기 또는 제거하기

- **잠시 끄기**는 **HMG-API → Mods → EV6 KOR Android Auto TBT (test)**에서 **Off**로 바꾼 뒤 **Apply changes**를 누릅니다.
- **삭제**는 같은 모드 상세 화면 아래의 **Remove EV6 KOR Android Auto TBT (test)**를 누르고 확인한 뒤 **Apply changes**를 누릅니다.

재시작 안내가 나오면 따르고, Android Auto 확인이 필요하면 폰 케이블을 다시 연결합니다. [HMG-API 모드 제거 안내](https://github.com/Open-HMG/HMG-API/wiki/Installing-and-removing-mods#removing) · [프로젝트 README](../README.md)
