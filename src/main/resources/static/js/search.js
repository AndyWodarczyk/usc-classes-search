 $(document).ready(function()
  {
    $('input[name="searchOption"]').on("change", function()
    {
      // local lookup no specific search criteria
      if ($(this).val()=='L')
      {
        $(".required-text").text("");
        $(".input-box").prop("disabled", true);
      } 

      // Remote lookup - set required fields
      else
      {
        $(".required-text").text("Required");
        $(".input-box").prop("disabled", false);
      }
  });
});


