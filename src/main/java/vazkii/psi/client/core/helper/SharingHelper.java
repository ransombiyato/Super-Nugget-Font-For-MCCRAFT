/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.core.helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.util.Util;

import vazkii.psi.common.Psi;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class SharingHelper {

	private static final String CLIENT_ID = "d5d2258f3526156";

	public static void uploadAndShare(String title, String export) {
		String url = uploadImage(title, export);

		try {
			String contents = "## " + title + "  \n" +
					"### [Image + Code](" + url + ")\n" +
					"(to get the code click the link, RES won't show it)\n" +
					"\n" +
					"---" +
					"\n" +
					"*REPLACE THIS WITH A DESCRIPTION OF YOUR SPELL  \n" +
					"Make sure you read the rules before posting. Look on the sidebar: https://www.reddit.com/r/psispellcompendium/  \n" +
					"Delete this part before you submit.*";

			String encodedContents = URLEncoder.encode(contents, StandardCharsets.UTF_8);
			String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

			String redditUrl = "https://old.reddit.com/r/psispellcompendium/submit?title=" + encodedTitle + "&text=" + encodedContents;
			Util.getPlatform().openUri(new URI(redditUrl));
		} catch (Exception e) {
			Psi.logger.error("Error when trying to create a reddit post", e);
		}
	}

	public static void uploadAndOpen(String title, String export) {
		String url = uploadImage(title, export);
		try {
			Util.getPlatform().openUri(new URI(url));
		} catch (Exception e) {
			Psi.logger.error("Error when trying to open uploaded image URL", e);
		}
	}

	public static String uploadImage(String title, String export) {
		try {
			String desc = "Spell Code:\n\n" + export;
			String form = "type=base64"
					+ "&image=" + URLEncoder.encode(takeScreenshot(), StandardCharsets.UTF_8)
					+ "&name=" + URLEncoder.encode(title, StandardCharsets.UTF_8)
					+ "&description=" + URLEncoder.encode(desc, StandardCharsets.UTF_8);
			HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.imgur.com/3/image"))
					.header("Authorization", "Client-ID " + CLIENT_ID)
					.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
					.POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
					.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			if(response.statusCode() / 100 != 2) {
				Psi.logger.warn("Imgur upload returned HTTP {}", response.statusCode());
				return "N/A";
			}
			JsonObject resJson = JsonParser.parseString(response.body()).getAsJsonObject();
			if(resJson.has("success") && resJson.get("success").getAsBoolean()) {
				JsonObject data = resJson.get("data").getAsJsonObject();
				String id = data.get("id").getAsString();

				return "https://imgur.com/" + id;
			}
		} catch (Exception e) {
			Psi.logger.error("Error when uploading image to imgur", e);
		}

		return "N/A";
	}

	public static String takeScreenshot() throws Exception {
		Minecraft mc = Minecraft.getInstance();

		try (NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
			byte[] bArray = image.asByteArray();
			return Base64.getEncoder().encodeToString(bArray);
		}
	}

}
