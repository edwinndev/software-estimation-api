<#macro layout>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Intecx InScope</title>
</head>
<body style="margin:0;padding:0;background:#f4f4f4;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background:#f4f4f4;padding:24px 0;">
    <tr>
      <td align="center">
        <table role="presentation" width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border:1px solid #e6e6e6;">
          <tr>
            <td style="padding:28px 32px 20px;text-align:center;border-bottom:3px solid #03DDAB;">
              <img src="cid:appLogo" alt="Intecx" width="200" height="64" style="display:block;margin:0 auto;border:0;outline:none;text-decoration:none;">
            </td>
          </tr>
          <tr>
            <td style="padding:28px 32px;color:#333333;font-size:15px;line-height:1.55;">
              <#nested>
            </td>
          </tr>
          <tr>
            <td style="padding:16px 32px;background:#fafafa;color:#777777;font-size:12px;text-align:center;">
              Intecx · InScope
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
</#macro>
