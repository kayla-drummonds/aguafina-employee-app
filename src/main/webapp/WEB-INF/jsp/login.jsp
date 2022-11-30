<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width" />
		<link
			rel="stylesheet"
			href="https://unpkg.com/spectre.css/dist/spectre.min.css" />
		<link
			rel="stylesheet"
			href="https://unpkg.com/spectre.css/dist/spectre-exp.min.css" />
		<link
			rel="stylesheet"
			href="https://unpkg.com/spectre.css/dist/spectre-icons.min.css" />
		<link rel="stylesheet" href="../static/css/styles.css" />
		<link rel="preconnect" href="https://fonts.googleapis.com" />
		<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
		<link
			href="https://fonts.googleapis.com/css2?family=Noto+Sans+Ethiopic&display=swap"
			rel="stylesheet" />
		<style>
			#login-container {
				background-color: #135c9e;
				color: #e3e3e3;
			}

			#login-container h3,
			#login-container a {
				color: #e3e3e3;
			}

			#login-container a:hover {
				color: #e3e3e3;
				text-decoration: underline #e3e3e3 2px;
			}

			label {
				color: #135c9e;
			}
		</style>
		<title>Login</title>
	</head>

	<body>
		<div class="container py-2 px-2">
			<div
				class="card p-centered col-6 col-sm-12 px-2 py-2"
				id="login-container">
				<div class="card-header">
					<h3 class="card-title text-center">Login</h3>
				</div>
				<div class="py-2 px-2">
					<!-- error message -->
					<c:if test="${param.error} != null">
						<div class="toast toast-error">Invalid username or password.</div>
					</c:if>

					<!-- logout message -->
					<c:if test="${param.logout} != null">
						<div class="toast toast-success">You have been logged out.</div>
					</c:if>

					<!-- registration successful message-->
					<c:if test="${param.success} != null">
						<div class="toast toast-success">
							You have been successfully registered. Please log in.
						</div>
					</c:if>
				</div>
				<div class="card-body">
					<form
						class="form-horizontal px-2 s-rounded"
						action="/api/login"
						method="post">
						<div class="columns">
							<div class="column col-12 py-2">
								<div class="input-group">
									<label class="form-label input-group-addon" for="username">
										Email:
									</label>
									<input
										type="text"
										class="form-input"
										id="username"
										name="username"
										placeholder="Email"
										autofocus="autofocus" />
								</div>
							</div>
							<div class="column col-12 py-2">
								<div class="input-group">
									<label class="form-label input-group-addon" for="password"
										>Password:</label
									>
									<input
										type="password"
										id="password"
										name="password"
										class="form-input"
										placeholder="Password" />
								</div>
							</div>
							<div class="column">
								<button class="btn btn-success p-centered" type="submit">
									Log In
								</button>
							</div>
						</div>
					</form>
				</div>
				<div class="card-footer text-center">
					<span
						>New user? Register as
						<a href="registration_employee.jsp">an employee</a> or
						<a href="registration_customer.jsp">a customer</a>.</span
					>
				</div>
			</div>
		</div>
		<div th:insert="~{footer :: copy}"></div>
	</body>
</html>
