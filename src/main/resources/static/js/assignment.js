function filterUsers() {
  var input, filter, users, user, name;
  input = document.getElementById("searchInput");
  filter = input.value.toUpperCase();
  users = document.getElementsByClassName("user");
  for (var i = 0; i < users.length; i++) {
    user = users[i];
    name = user.getElementsByClassName("card-title")[0];
    if (name.innerText.toUpperCase().indexOf(filter) > -1) {
      user.style.display = "";
    } else {
      user.style.display = "none";
    }
  }
}
function filterTableUsers() {
  // Declare variables
  var input, filter, table, tr, td, i, txtValue;
  input = document.getElementById("searchInputTable");
  filter = input.value.toUpperCase();
  table = document.getElementById("usersTable");
  tr = table.getElementsByTagName("tr");

  // Loop through all table rows, and hide those that don't match the search query
  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[0]; // Assuming the name is in the first column, adjust index if necessary
    if (td) {
      txtValue = td.textContent || td.innerText;
      if (txtValue.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      } else {
        tr[i].style.display = "none";
      }
    }
  }
}

function switchLayout() {
  var cardView = document.getElementById("cardView");
  var tableView = document.getElementById("tableView");
  var cardDeck = document.getElementsByClassName("card-deck")[0];
  var table = document.getElementById("usersTable");

  if (tableView.style.display === "none") {
    cardView.style.display = "none";
    tableView.style.display = "block";
    cardDeck.style.display = "none";
    table.style.display = "table";
  } else {
    cardView.style.display = "block";
    tableView.style.display = "none";
    cardDeck.style.display = "flex";
    table.style.display = "none";
  }
}
