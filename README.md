# Study Group Platform

## 1. 개요 :  프로그램의 목적과 대상 사용자를 설명합니다. 누구를 위한 프로그램인지, 무엇을 하는 프로그램인지를 간단히 소개합니다.

### 1.1 목적
스터디 그룹 플랫폼은 학생들이 자신이 속할 스터디 그룹을 생성하고, 참가할 수 있는 Java 기반의 데스크탑 애플리케이션입니다. 이 프로그램은 사용자가 본인이 원하는 분야나 과목에 맞는 그룹을 쉽게 찾아 참가할 수 있도록 도와줍니다. 또한, 사용자에게 직관적인 GUI를 제공하여 스터디 그룹을 효율적으로 관리할 수 있습니다.

### 1.2 대상
이 프로그램은 대학생들을 주요 대상으로 합니다. 특히 특정 과목에 대해 스터디 그룹을 만들거나 참가하려는 학생들에게 유용하게 활용될 수 있습니다. 

## 2. 프로그램의 중요성 및 필요성

### 2.1 중요성
스터디 그룹 플랫폼은 학생들이 학업을 함께 할 동료를 찾고, 과목별로 효율적으로 스터디를 진행할 수 있도록 돕는 중요한 도구입니다. 또한, 같은 목표를 가진 사람들끼리 모여 목표 수행을 보다 효율적으로 가능케해줍니다. 학생들이 공부하는 과목에 맞는 그룹을 찾는 것은 효율적인 학습 환경을 제공하는 데 필수적인 요소입니다. 이 프로그램은 학생들에게 학습의 동기부여를 제공하고, 그룹 관리의 편의성을 향상시킵니다.

### 2.2 필요성
대학생들은 많은 과목에서 공부하지만, 과목별로 효율적인 학습 환경을 제공하는 그룹을 찾기 어려운 경우가 많습니다. 이 프로그램은 과목별로 스터디 그룹을 관리하고, 검색할 수 있는 기능을 제공하여 학생들이 자신에게 맞는 스터디 그룹을 쉽게 찾을 수 있게 합니다. 또한, 스터디 그룹을 생성하고 참가하는 과정이 간단하고 직관적이기 때문에 누구나 쉽게 사용할 수 있습니다.

## 3. 프로그램 수행 절차

### 3.1 다이어그램
프로그램의 전체 흐름은 크게 **스터디 그룹 생성**, **그룹 리스트 검색**, **그룹 참가** 기능으로 나눌 수 있습니다. 이 각 기능은 순차적으로 실행되며, 각각의 기능은 **CSV 파일 읽기/쓰기**와 연결됩니다. 

- **스터디 그룹 생성**: 사용자는 이름, 학번, 그룹명, 과목을 입력하여 새로운 스터디 그룹을 생성할 수 있습니다.
- **그룹 리스트 검색**: 사용자는 특정 과목을 입력하여 해당 과목에 속한 스터디 그룹 목록을 볼 수 있습니다.
- **그룹 참가**: 사용자는 리스트에서 원하는 그룹을 선택하고 참가 신청을 할 수 있습니다.

```plaintext
+-------------------+      +------------------------+
|  사용자 입력    |----->|   스터디 그룹 생성   |
+-------------------+      +------------------------+
                           |
                           V
                      +-----------------------+
                      |   그룹 리스트 확인    |
                      +-----------------------+
                           |
                           V
                      +---------------------+
                      |   그룹 참가 신청    |
                      +---------------------+

### 3.2 클래스 다이어그램
![스크린샷 2024-12-26 000540](https://github.com/user-attachments/assets/f1060701-9af5-4b78-ad61-eda74980e7a8)


### 3.3 절차 설명  
그룹 생성: 사용자는 자신의 이름, 학번, 그룹명, 과목을 입력하여 새로운 스터디 그룹을 생성합니다. 이 정보는 StudyGroup 객체로 저장되며, 학생은 Student 객체로 그룹에 추가됩니다.
그룹 리스트 검색: 사용자는 과목명으로 검색하여 해당 과목에 대한 스터디 그룹을 찾을 수 있습니다. StudyGroupPlatform 클래스의 검색 기능이 이를 처리합니다.
그룹 참가: 사용자는 선택한 그룹에 참가할 수 있습니다. 이 과정에서 사용자의 정보는 해당 그룹에 Student 객체로 추가됩니다.
CSV 파일 저장: 그룹 생성 및 변경 사항은 CSV 파일에 저장되어, 프로그램 종료 후에도 데이터가 유지됩니다.

### 4. 느낌점
이 프로그램을 개발하면서, GUI와 입출력 처리의 중요성을 다시 한 번 깨달았습니다. 특히, 사용자 경험을 향상시키는 데 있어 직관적인 인터페이스가 중요하다는 점을 실감했습니다. 또한, 데이터를 파일로 저장하고 불러오는 과정에서 CSV 파일을 사용하는 방식의 장점과 단점을 경험하면서, 데이터 관리의 효율성에 대해 깊이 생각하게 되었습니다.

이번 프로젝트를 통해 객체지향 프로그래밍의 개념을 더욱 잘 이해하게 되었으며, 실제로 GUI 애플리케이션을 구현하는 경험을 쌓을 수 있었습니다. 또한, 프로그램의 유지보수성과 확장성을 고려한 설계가 얼마나 중요한지에 대해서도 많은 것을 배울 수 있었습니다.
