import cors from "cors";
import dotenv from "dotenv";
import express, {
  Application,
  NextFunction,
  Request,
  Response,
  Router,
} from "express";
import api from "./rest/api";
import bodyParser from "body-parser";
import consola from "consola";

dotenv.config();
const app: Application = express();
const router: Router = express.Router();
const PORT: string | undefined = process.env.PORT;
router.use((req: Request, res: Response, next: NextFunction) => {
  consola.info({
    message: `${req.method} request from ${req.hostname} [${req.ip}]`,
    badge: true,
  });
  res.header("Access-Control-Allow-Origin", "*");
  res.header(
    "Access-Control-Allow-Headers",
    "Origin, X-Requested-With, Content-Type, Accept"
  );
  next();
});
app.use(cors());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use("/", api(router));
app.listen(PORT, () => {
  consola.ready({
    message: `Server listening on port ${PORT}`,
    badge: true,
  });
});
