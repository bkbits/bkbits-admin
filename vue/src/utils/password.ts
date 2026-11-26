import JSEncrypt from "jsencrypt";
import { authApi } from "@/api";

/** 公钥缓存：同一页面会话内只请求一次 */
let publicKeyPromise: Promise<string> | null = null;

/** 获取 RSA 公钥（失败时清空缓存，便于重试） */
export function getPublicKeyCached(): Promise<string> {
  if (!publicKeyPromise) {
    publicKeyPromise = authApi
      .getPublicKey()
      .then((result) => {
        if (!result.ok || !result.data) {
          throw new Error(result.message || "获取公钥失败");
        }
        return result.data;
      })
      .catch((error: unknown) => {
        publicKeyPromise = null;
        throw error;
      });
  }
  return publicKeyPromise;
}

/** 使用 RSA 公钥加密密码，返回 Base64 密文 */
export async function encryptPassword(password: string): Promise<string> {
  const publicKey = await getPublicKeyCached();
  const encryptor = new JSEncrypt();
  encryptor.setPublicKey(publicKey);
  const encrypted = encryptor.encrypt(password);
  if (!encrypted) {
    throw new Error("密码加密失败，请重试");
  }
  return encrypted;
}
