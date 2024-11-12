// 種目を取得し、セレクトボックスに追加する関数
function fetchAndDisplayExercisesWithCustomOptions()  {
    const bodyPartId = document.getElementById('bodyPartsSelect').value;
    const exerciseSelect = document.getElementById("exerciseSelect");

    // 既存のオプションをクリア
    exerciseSelect.innerHTML = '<option value="">選択してください</option>';

    if (bodyPartId) {
        fetch(`/api/exercise?bodyPartId=${bodyPartId}`)
            .then(response => response.json())
            .then(data => {
                // 動的に取得した種目を追加
                if (data.length > 0) {
                    data.forEach(item => {
                        const option = document.createElement('option');
                        option.value = item.id;
                        option.text = item.name;
                        exerciseSelect.appendChild(option);
                    });
                }

                // 常に「種目削除」と「種目追加」オプションを追加
                addCustomOptions(exerciseSelect);
            })
            .catch(error => console.error('Error fetching exercises:', error));
    } else {
        // 動的取得ができない場合でも、必ず「種目削除」と「種目追加」を追加
        addCustomOptions(exerciseSelect);
    }
}

// 「種目追加」と「種目削除」のオプションを追加する関数
function addCustomOptions(selectElement) {
    const addOption = document.createElement('option');
    addOption.value = "add";
    addOption.text = "種目追加";
    addOption.style.backgroundColor = "red";
    addOption.style.color = "white";
    selectElement.appendChild(addOption);

    const deleteOption = document.createElement('option');
    deleteOption.value = "delete";
    deleteOption.text = "種目削除";
    deleteOption.style.backgroundColor = "gray";
    deleteOption.style.color = "white";
    selectElement.appendChild(deleteOption);
}

// ページが読み込まれた際に「種目追加」「種目削除」を必ず追加する
document.addEventListener("DOMContentLoaded", function () {
    const exerciseSelect = document.getElementById('exerciseSelect');

    // ページ読み込み時に種目追加・削除オプションを追加
    addCustomOptions(exerciseSelect);

    // セレクトボックスの変更時に実行するイベントリスナーを設定
    exerciseSelect.addEventListener('change', function () {
        // この場所で最新の bodyPartId を取得
        const bodyPartId = document.getElementById('bodyPartsSelect').value;
        const selectedValue = exerciseSelect.value;

        if (selectedValue === "delete") {
            // 「種目削除」を選択した場合に画面遷移
                window.location.href = `/exercise/delete/types?bodyPartId=${bodyPartId}`;
            
        } else if (selectedValue === "add") {
            // 「種目追加」を選択した場合に画面遷移
                window.location.href = `/exercise/add/types?bodyPartId=${bodyPartId}`;
        }
    });
});

