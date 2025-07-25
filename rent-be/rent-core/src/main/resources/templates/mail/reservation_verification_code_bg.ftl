<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Код за потвърждение</title>
</head>
<body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
<table cellspacing="0" cellpadding="0" border="0" width="100%" bgcolor="#f4f4f4">
	<tr>
		<td align="center">
			<table cellspacing="0" cellpadding="0" border="0" width="800" bgcolor="#ffffff"
				   style="margin: 20px auto; padding: 30px;">
				<tr>
					<td align="center" style="
						font-size: 0px;
						padding: 30px 25px;
						word-break: break-word;
					  ">
						<div style="
						  font-family: Noto Sans SC, Arial, sans-serif;
						  font-size: 16px;
						  font-weight: 700;
						  letter-spacing: 0;
						  line-height: 23px;
						  text-align: center;
						  color: #020e36;
						">
							Привет, ${fullName} (${email})
						</div>
					</td>
				</tr>
				<tr>
					<td align="center" style="
						font-size: 0px;
						padding: 10px 25px;
						word-break: break-word;
					  ">
						<div style="
						  font-family: Noto Sans SC, Arial, sans-serif;
						  font-size: 14px;
						  font-weight: 400;
						  letter-spacing: 0;
						  line-height: 20px;
						  text-align: center;
						  color: #020e36;
						">
							За да продължите резервацията, моля, потвърдете имейла си с кода по-долу:
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td align="center" class="code-text" style="
                                    font-size: 0px;
                                    padding: 30px 25px;
                                    word-break: break-word;
                                  ">
									<div style="
                                      font-family: Noto Sans SC, Arial, sans-serif;
                                      font-size: 62px;
                                      font-weight: 700;
                                      letter-spacing: 0;
                                      line-height: 110px;
                                      text-align: center;
                                      color: rgba(2, 14, 54, 0.8);
                                    ">
										${code}
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td align="center" style="
						font-size: 0px;
						padding: 30px 25px;
						word-break: break-word;
					  ">
						<div style="
						  font-family: Noto Sans SC, Arial, sans-serif;
						  font-size: 12px;
						  font-weight: 400;
						  letter-spacing: 0;
						  line-height: 17px;
						  text-align: center;
						  color: rgba(2, 14, 54, 0.6);
						">
							Кодът е валиден ${exp} минути. Моля, не разкривайте този код на други.<br/>
							Ако не сте направили тази заявка, моля, игнорирайте този имейл.
						</div>
					</td>
				</tr>
				<tr>
					<td align="center" style="
						font-size: 0px;
						padding: 30px 25px;
						word-break: break-word;
					  ">
						<div style="
						  font-family: Noto Sans SC, Arial, sans-serif;
						  font-size: 14px;
						  font-weight: 400;
						  letter-spacing: 0;
						  line-height: 20px;
						  text-align: center;
						  color: rgba(2, 14, 54, 0.6);
						">
							© ${year} bungala-rayat. All Rights Reserved.
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>