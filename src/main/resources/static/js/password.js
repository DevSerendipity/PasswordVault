var input = document.getElementById("password");
if(input){
input.addEventListener("keypress", function(event) {
  if (event.key === "Enter") {
    console.log(input.value);
  }
},false);
}