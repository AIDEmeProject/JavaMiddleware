function loadFileFromInputFile(inputId, onFileLoaded){


    if (document.getElementById(inputId).files.length == 0 ){
        alert('Please select a trace file ')
        return
    }

    var fileReader = new FileReader()
    const file = document.getElementById(inputId)
    fileReader.onload = onFileLoaded
    fileReader.readAsText(file.files[0])



}

export default loadFileFromInputFile