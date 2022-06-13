module.exports = (router, deps) => {
  const { multiparty, request, fs } = deps;

  router.post("/api", (req, res) => {
    const formPromise = new Promise((resolve) => {
      let multipartForm = new multiparty.Form();
      multipartForm.parse(req, (err, fields, files) => {
        resolve({
          fields,
          files,
        });
      });
    });
    formPromise.then(({ fields, files }) => {
      const { form: json, template } = fields;
      const { ca, key, p12 } = files;
      const form = JSON.parse(json);

      console.log(form);
      console.log(files);
      const xmlPayload = Object.entries(form.requestPayloadFields).reduce(
        (acc, [k, v]) => acc.replace(`@@${k}@@`, v),
        template[0]
      );
      var options = {
        url: form.requestHeaderFields.url,
        headers: {
          "Content-Type": form.requestHeaderFields["content-type"],
        },
        agentOptions: {
          ca: fs.readFileSync(ca[0].path),
          key: fs.readFileSync(key[0].path),
          // Or use `pfx` property replacing `cert` and `key` when using private key, certificate and CA certs in PFX or PKCS12 format:
          pfx: fs.readFileSync(p12[0].path),
          securityOptions: "SSL_OP_NO_SSLv3",
          passphrase: "logitech",
        },
        body: xmlPayload,
      };

      request.post(options, (error, response, body) => {
        const clientResponse = {
          apiStatus: 200,
          adaptorStatus: response ? response.statusCode : 400,
          adaptorResponse: error ? error.code : body,
        };
        res.json(clientResponse).end();
      });
    });
  });
  return router;
};
