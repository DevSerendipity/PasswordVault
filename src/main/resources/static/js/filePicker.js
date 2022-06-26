const output = document.getElementById('output');
const filePicker = document.getElementById('file-picker');

filePicker.addEventListener('change', (event) => {
  const files = event.target.files;
  output.textContent = '';

  for (const file of files) {
    document.getElementById('output').innerHTML =
                        document.getElementById('output').innerHTML + '<br /> ' +
                            file.name;
  }
})
