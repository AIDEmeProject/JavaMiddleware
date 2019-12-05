function loadCSVFromInputFile(inputId, onFileLoaded){


    var fileReader = new FileReader()
    const file = document.getElementById(inputId)
    fileReader.onload = onFileLoaded
    fileReader.readAsText(file.files[0])

}

export default loadCSVFromInputFile