function fetchExercises() {
    const bodyPartId = document.getElementById('bodyPartsSelect').value;
    const exerciseSelect = document.getElementById("exerciseSelect");

    if (bodyPartId) {
        fetch(`/api/exercise?bodyPartId=${bodyPartId}`)
            .then(response => response.json())
            .then(data => {
                exerciseSelect.innerHTML = '<option value="">選択してください</option>';
                data.forEach(item => {
                    const option = document.createElement('option');
                    option.value = item.id;
                    option.text = item.name;
                    exerciseSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Error fetching exercises:', error));
    } else {
        exerciseSelect.innerHTML = '<option value="">選択してください</option>';
    }
}
