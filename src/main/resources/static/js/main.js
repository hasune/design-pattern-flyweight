document.addEventListener('DOMContentLoaded', function() {
    // 폼 제출 시 유효성 검사
    const documentForm = document.getElementById('documentForm');
    if (documentForm) {
        documentForm.addEventListener('submit', function(e) {
            const title = document.getElementById('title').value.trim();
            const content = document.getElementById('content').value.trim();
            
            if (!title || !content) {
                e.preventDefault();
                alert('제목과 내용을 모두 입력해주세요.');
                return false;
            }
            
            if (content.length > 1000) {
                e.preventDefault();
                alert('내용은 1000자를 초과할 수 없습니다.');
                return false;
            }
        });
    }
    
    // 캐시 정보 업데이트 애니메이션
    const cacheInfo = document.querySelector('.cache-info');
    if (cacheInfo) {
        cacheInfo.style.opacity = '0';
        setTimeout(function() {
            cacheInfo.style.opacity = '1';
            cacheInfo.style.transition = 'opacity 0.5s ease-in';
        }, 100);
    }
    
    // 문서 렌더링 결과 강조
    const renderedContent = document.querySelector('.rendered-content');
    if (renderedContent) {
        const chars = renderedContent.querySelectorAll('div');
        chars.forEach((char, index) => {
            setTimeout(() => {
                char.style.background = '#fffacd';
                setTimeout(() => {
                    char.style.background = 'transparent';
                }, 500);
            }, index * 50);
        });
    }
});

// 캐시 클리어 확인
function confirmClearCache() {
    return confirm('정말로 문자 캐시를 초기화하시겠습니까?');
}
