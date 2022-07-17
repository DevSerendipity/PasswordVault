document.getElementById('filepicker').addEventListener(
  'change',
  function (event) {
    const files = event.target.files

    for (let i = 0; i < files.length; i++) {
      const item = document.createElement('li')
      item.innerHTML = files[i].webkitRelativePath
      const source = item.innerHTML
      $('#result').val(source)
    }
  },
  false
)
