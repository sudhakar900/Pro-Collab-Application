var projectId = /*[[${projectId}]]*/ null; // Access projectId passed from controller
console.log("testing");
function addEmployeeToProject() {
  var form = document.getElementById("addEmployeeForm");
  var checkboxes = form.getElementsByClassName("employee-checkbox");
  var selectedUsers = [];

  // Iterate over checkboxes to find selected users
  for (var i = 0; i < checkboxes.length; i++) {
    if (checkboxes[i].checked) {
      selectedUsers.push(checkboxes[i].value);
    }
  }
  console.log(selectedUsers);

  // If no users are selected, return
  if (selectedUsers.length === 0) {
    alert("Please select at least one employee.");
    return;
  }

  // Perform addition of selected employees to the project
  fetch("/projects/" + projectId + "/employees", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(selectedUsers),
  })
    .then((response) => {
      if (response.ok) {
        location.reload(); // Reload the page after successful addition
      } else {
        alert("Failed to add employee(s) to project.");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to add employee(s) to project.");
    });
}

// JavaScript function to handle removing employee from project
function removeEmployeeFromProject(employeeId) {
  if (
    confirm("Are you sure you want to remove this employee from the project?")
  ) {
    fetch("/projects/" + projectId + "/employees/" + employeeId, {
      method: "DELETE",
    })
      .then((response) => {
        if (response.ok) {
          location.reload(); // Reload the page after successful removal
        } else {
          alert("Failed to remove employee from project.");
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("Failed to remove employee from project.");
      });
  }
}

// JavaScript function to filter available users based on search input
function filterAvailableUsers() {
  var input, filter, table, tr, td, i, txtValue;
  input = document.getElementById("searchInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("availableUsersTable");
  tr = table.getElementsByTagName("tr");
  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[1]; // Index 1 for the "Name" column
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

// JavaScript function to filter project employees based on search input
function filterProjectEmployees() {
  var input, filter, table, tr, td, i, txtValue;
  input = document.getElementById("searchProjectInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("projectEmployeesTable");
  tr = table.getElementsByTagName("tr");
  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[1]; // Index 1 for the "Name" column
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

// JavaScript function to remove selected employees
function removeSelectedEmployees() {
  var form = document.getElementById("projectEmployeesTable");
  var checkboxes = form.getElementsByClassName("delete-employee-checkbox");
  var selectedEmployees = [];

  // Iterate over checkboxes to find selected employees
  for (var i = 0; i < checkboxes.length; i++) {
    if (checkboxes[i].checked) {
      selectedEmployees.push(checkboxes[i].value);
    }
  }

  // If no employees are selected, return
  if (selectedEmployees.length === 0) {
    alert("Please select at least one employee to delete.");
    return;
  }

  // Perform deletion of selected employees from the project
  if (confirm("Are you sure you want to delete the selected employees?")) {
    fetch("/projects/" + projectId + "/employees", {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(selectedEmployees),
    })
      .then((response) => {
        if (response.ok) {
          location.reload(); // Reload the page after successful deletion
        } else {
          alert("Failed to delete selected employees from the project.");
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("Failed to delete selected employees from the project.");
      });
  }
}
