# 사명감 조 GDG 통합 페이지 프로젝트

## 1. 작업 시작 전, 먼저 Issue를 생성해주세요!

1. `Issues` 탭에서 `New issue`로 새로운 Issue를 생성합니다.
2. **무엇을 할지 바로 이해될 수 있는 제목**을 붙여주세요.
    - 예시: `회원가입 API 구현`, `로그인 페이지 UI`, `헤더 공통 컴포넌트 추가`


## 2. Issue 번호를 기반으로 브랜치 이름을 만들어주세요!

예: **회원가입 API를 생성한다고 가정**해봅시다. Issue 번호는 `#1`이라고 할게요.

1. 작업할 repository를 clone 해옵니다.
    
    ```bash
    git clone https://github.com/sense-of-duty/Backend.git (클론 하려는 레포지토리 주소)
    cd REPO_NAME
    ```
    
    이미 clone을 해뒀다면, 아래처럼 최신 코드를 가져오시면 됩니다.
    
    ```bash
    git checkout main
    git pull origin main
    ```
    

1. !!!! 중요 !!!! **반드시 브랜치를 생성해서 작업합니다 !!**
    
    ```bash
    git checkout -b feat/1-register-api
    ```
    
    - 브랜치 규칙: `type/이슈번호-작업-설명`
    - 앞의 숫자 `1`은 **Issue 번호**를 뜻합니다!
    - `feat/` : 새로운 기능
    - `fix/` : 버그 수정
    - `refactor/` : 리팩토링
    - `chore/` : 설정, 빌드, 기타 잡일
    
2. Commit 메시지는 아래 컨벤션을 지켜주세요.
    
    ```bash
    feat: create register api (#1)
    ```
    
    - 형식: `<type>: <메시지> (#이슈번호)`
    - `feat`, `fix`, `refactor`, `chore` 등 사용
    


## 3. 작업한 내용을 푸시할 때

아래와 같이 본인이 만든 브랜치 이름으로 push 해주세요.

```bash
git push origin feat/1-register-api
```


## 4. 작업 완료 후 PR & 리뷰

1. GitHub에서 `Compare & pull request` 버튼을 눌러 PR을 생성합니다.
2. PR 제목은 커밋과 비슷한 형태로 작성해주세요.
    - 예: `feat: create register api (#1)`
3. PR에 관련 Issue를 연결해주세요.
    - 오른쪽 하단에 Development를 클릭해서 작업한 Issue 번호를 연결합니다.
    - 연결 후 PR 날리면 merge될 때 자동으로 Issue 닫힙니다 !
    
4. **2명에게 Approve**를 받으면 Merge하셔도 됩니다.
    - 기본 규칙: PR 작성자가 본인 PR을 Merge합니다.


## 5. 백엔드 개발 환경은 JAVA 17 버전입니다. 버전에 맞게 개발해주세요.
