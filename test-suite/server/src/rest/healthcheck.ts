import { Request as Request, Response, Router } from "express";

const healthcheck = (router: Router) => {
  router.get("/", (req: Request, res: Response) => {
    res.sendStatus(200).end();
  });
  return router;
};

export default healthcheck;
