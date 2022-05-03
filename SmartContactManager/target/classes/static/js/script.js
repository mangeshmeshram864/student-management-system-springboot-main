
console.log("this is js");
const togglesidebar = () =>
{
	if($(".sidebar").is(":visible"))
	{
		//if visible then close
		
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
	}
	else
	{
		//if not visible then show visible
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};

const search =()=>
{

  let query=$("#search-input").val();
  
  
  if(query=="")
  {
	$(".search-result").hide();
}else{
	console.log(query );
	
	//sending request to server
	let url=`http://localhost:8092/search/${query}`;
	
	fetch(url)
	.then((response)=>{
		return response.json();
	})
	.then((data)=>{
	//data....
	//console.log(data);
	let text=`<div class='list-group'>`;
	
	data.forEach((contact)=>{
		text+=`<a href='#' class='list-group-item list-group-action' >${contact.name}</a>`
		
	});
	text +=`</div>`;
	
	
	$(".search-result").html(text);
	$(".search-result").show();
	
});

}
};