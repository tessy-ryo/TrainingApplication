function loadExercisesByBodyPart() {
    const bodyPartId = document.getElementById('bodyPartsSelect').value;
    const exerciseBox = document.getElementById('exerciseBox');

    // 既存の表示をクリア
    exerciseBox.innerHTML = '';

    // 部位が選択されているか確認
    if (bodyPartId) {
        // APIリクエストを送信
        fetch(`/api/exercise?bodyPartId=${bodyPartId}`)
            .then(response => response.json())
            .then(data => {
                if (data.length > 0) {
                    // <ul> リスト要素を作成
                    const ul = document.createElement('ul');
                    
                    // 種目リストを動的に生成して <li> 要素を追加
                    data.forEach(item => {
                        const li = document.createElement('li');
                        li.textContent = item.name;
                        ul.appendChild(li);
                    });

                    // リストを表示する <div> に追加
                    exerciseBox.appendChild(ul);
                } else {
                    // 種目が存在しない場合のメッセージ
                    exerciseBox.innerHTML = '<p>・登録されている種目がありません。</p>';
                }
            })
            .catch(error => {
                console.error('Error fetching exercises:', error);
                exerciseBox.innerHTML = '<p>・種目の取得に失敗しました。</p>';
            });
    } else {
        exerciseBox.innerHTML = '<p>・部位を選択してください。</p>';
    }
}



