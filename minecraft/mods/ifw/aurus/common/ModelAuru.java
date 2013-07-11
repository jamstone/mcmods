package mods.ifw.aurus.common;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelAuru extends ModelBase {
	// fields
	ModelRenderer SpinesU;
	ModelRenderer ForeR;
	ModelRenderer SpinesM;
	ModelRenderer SpinesB;
	ModelRenderer Body;
	ModelRenderer TailM;
	ModelRenderer TailB;
	ModelRenderer ForeL;
	ModelRenderer RearR;
	ModelRenderer RearL;
	ModelRenderer EarR;
	ModelRenderer EarL;
	ModelRenderer Head;
	ModelRenderer WingBoneR;
	ModelRenderer WingFlapR;
	ModelRenderer WingBoneL;
	ModelRenderer WingFlapL;
	// FOR SLEEPING MODEL
	ModelRenderer SSpinesU;
	ModelRenderer SForeR;
	ModelRenderer SSpinesM;
	ModelRenderer SSpinesB;
	ModelRenderer SBody;
	ModelRenderer STailM;
	ModelRenderer STailB;
	ModelRenderer SForeL;
	ModelRenderer SRearR;
	ModelRenderer SRearL;
	ModelRenderer SEarR;
	ModelRenderer SEarL;
	ModelRenderer SHead;
	ModelRenderer SWingBoneR;
	ModelRenderer SWingFlapR;
	ModelRenderer SWingBoneL;
	ModelRenderer SWingFlapL;

	// WAKING HEAD

	ModelRenderer WHead;

	float[] color;

	public ModelAuru(float red, float green, float blue) {

		textureWidth = 128;
		textureHeight = 64;

		this.color = new float[] { red, green, blue };

		SpinesU = new ModelRenderer(this, 57, 18);
		SpinesU.addBox(0F, -3F, -1F, 0, 12, 4);
		SpinesU.setRotationPoint(0F, -1F, 0F);
		SpinesU.setTextureSize(128, 64);
		SpinesU.mirror = true;
		setRotation(SpinesU, 0F, 0F, 0F);
		ForeR = new ModelRenderer(this, 72, 29);
		ForeR.addBox(-1F, -1F, -1F, 2, 6, 2);
		ForeR.setRotationPoint(-2F, -2F, -2F);
		ForeR.setTextureSize(128, 64);
		ForeR.mirror = true;
		setRotation(ForeR, -0.5235988F, 0F, 0F);
		SpinesM = new ModelRenderer(this, 66, 18);
		SpinesM.addBox(0F, 4F, -3F, 0, 14, 2);
		SpinesM.setRotationPoint(0F, -1F, 0F);
		SpinesM.setTextureSize(128, 64);
		SpinesM.mirror = true;
		setRotation(SpinesM, 0.5235988F, 0F, 0F);
		SpinesB = new ModelRenderer(this, 60, 9);
		SpinesB.addBox(0F, 10F, 14F, 0, 6, 2);
		SpinesB.setRotationPoint(0F, -1F, 0F);
		SpinesB.setTextureSize(128, 64);
		SpinesB.mirror = true;
		setRotation(SpinesB, -0.5235988F, 0F, 0F);
		Body = new ModelRenderer(this, 36, 18);
		Body.addBox(-3F, -3F, -3F, 6, 12, 4);
		Body.setRotationPoint(0F, -1F, 0F);
		Body.setTextureSize(128, 64);
		Body.mirror = true;
		setRotation(Body, 0F, 0F, 0F);
		TailM = new ModelRenderer(this, 70, 0);
		TailM.addBox(-2F, 4F, -5F, 4, 14, 2);
		TailM.setRotationPoint(0F, -1F, 0F);
		TailM.setTextureSize(128, 64);
		TailM.mirror = true;
		setRotation(TailM, 0.5235988F, 0F, 0F);
		TailB = new ModelRenderer(this, 53, 9);
		TailB.addBox(-1F, 12F, 13F, 2, 6, 1);
		TailB.setRotationPoint(0F, -1F, 0F);
		TailB.setTextureSize(128, 64);
		TailB.mirror = true;
		setRotation(TailB, -0.5235988F, 0F, 0F);
		ForeL = new ModelRenderer(this, 72, 29);
		ForeL.addBox(-1F, -1F, -1F, 2, 6, 2);
		ForeL.setRotationPoint(2F, -2F, -2F);
		ForeL.setTextureSize(128, 64);
		ForeL.mirror = true;
		setRotation(ForeL, -0.5235988F, 0F, 0F);
		RearR = new ModelRenderer(this, 71, 17);
		RearR.addBox(-1F, -1F, -1F, 2, 8, 3);
		RearR.setRotationPoint(-2F, 8F, -1F);
		RearR.setTextureSize(128, 64);
		RearR.mirror = true;
		setRotation(RearR, -0.2617994F, 0F, 0F);
		RearL = new ModelRenderer(this, 71, 17);
		RearL.addBox(-1F, -1F, -1F, 2, 8, 3);
		RearL.setRotationPoint(2F, 8F, -1F);
		RearL.setTextureSize(128, 64);
		RearL.mirror = true;
		setRotation(RearL, -0.2617994F, 0F, 0F);
		EarR = new ModelRenderer(this, 53, 0);
		EarR.addBox(-4F, -5F, 1F, 2, 2, 6);
		EarR.setRotationPoint(0F, -4F, -3F);
		EarR.setTextureSize(128, 64);
		EarR.mirror = true;
		setRotation(EarR, 0.5235988F, 0F, 0F);
		EarL = new ModelRenderer(this, 53, 0);
		EarL.addBox(2F, -5F, 1F, 2, 2, 6);
		EarL.setRotationPoint(0F, -4F, -3F);
		EarL.setTextureSize(128, 64);
		EarL.mirror = true;
		setRotation(EarL, 0.5235988F, 0F, 0F);
		Head = new ModelRenderer(this, 0, 0);
		Head.addBox(-3F, -4F, -8F, 6, 4, 12);
		Head.setRotationPoint(0F, -4F, -3F);
		Head.setTextureSize(128, 64);
		Head.mirror = true;
		setRotation(Head, 0.5235988F, 0F, 0F);
		WingBoneR = new ModelRenderer(this, 0, 20);
		WingBoneR.addBox(-2F, -1F, -1F, 16, 2, 2);
		WingBoneR.setRotationPoint(-4F, -2F, 0F);
		WingBoneR.setTextureSize(128, 64);
		WingBoneR.mirror = true;
		setRotation(WingBoneR, -0.5235988F, -2.617994F, 0.2617994F);
		WingFlapR = new ModelRenderer(this, 0, 35);
		WingFlapR.addBox(-3F, 1F, 0F, 17, 8, 1);
		WingFlapR.setRotationPoint(-4F, -2F, 0F);
		WingFlapR.setTextureSize(128, 64);
		WingFlapR.mirror = true;
		setRotation(WingFlapR, -0.5235988F, -2.617994F, 0.2617994F);
		WingBoneL = new ModelRenderer(this, 0, 20);
		WingBoneL.addBox(-2F, -1F, -1F, 16, 2, 2);
		WingBoneL.setRotationPoint(4F, -2F, 0F);
		WingBoneL.setTextureSize(128, 64);
		WingBoneL.mirror = true;
		setRotation(WingBoneL, 0.5235988F, -0.5235988F, -0.2617994F);
		WingFlapL = new ModelRenderer(this, 0, 35);
		WingFlapL.addBox(-3F, 1F, 0F, 17, 8, 1);
		WingFlapL.setRotationPoint(4F, -2F, 0F);
		WingFlapL.setTextureSize(128, 64);
		WingFlapL.mirror = true;
		setRotation(WingFlapL, 0.5235988F, -0.5235988F, -0.2617994F);

		int yOff = 19;

		SSpinesU = new ModelRenderer(this, 57, 18);
		SSpinesU.addBox(0F, -3F, -1F, 0, 12, 4);
		SSpinesU.setRotationPoint(0F, 1F + yOff, -1F);
		SSpinesU.setTextureSize(128, 64);
		SSpinesU.mirror = true;
		setRotation(SSpinesU, 1.570796F, 0.5235988F, 0F);
		SForeR = new ModelRenderer(this, 72, 29);
		SForeR.addBox(-1F, -1F, -1F, 2, 6, 2);
		SForeR.setRotationPoint(-3F, 4F + yOff, -3F);
		SForeR.setTextureSize(128, 64);
		SForeR.mirror = true;
		setRotation(SForeR, -1.570796F, 0F, 0F);
		SSpinesM = new ModelRenderer(this, 66, 18);
		SSpinesM.addBox(0F, -1F, 0F, 0, 14, 2);
		SSpinesM.setRotationPoint(4F, 0F + yOff, 5F);
		SSpinesM.setTextureSize(128, 64);
		SSpinesM.mirror = true;
		setRotation(SSpinesM, 1.396263F, 2.094395F, 0F);
		SSpinesB = new ModelRenderer(this, 60, 9);
		SSpinesB.addBox(0F, -2F, 0F, 0, 6, 2);
		SSpinesB.setRotationPoint(15F, 3F + yOff, -1F);
		SSpinesB.setTextureSize(128, 64);
		SSpinesB.mirror = true;
		setRotation(SSpinesB, 1.396263F, 3.191625F, 0F);
		SBody = new ModelRenderer(this, 36, 18);
		SBody.addBox(-3F, -3F, -3F, 6, 12, 4);
		SBody.setRotationPoint(0F, 1F + yOff, -1F);
		SBody.setTextureSize(128, 64);
		SBody.mirror = true;
		setRotation(SBody, 1.570796F, 0.5235988F, 0F);
		STailM = new ModelRenderer(this, 70, 0);
		STailM.addBox(-2F, -1F, -2F, 4, 14, 2);
		STailM.setRotationPoint(4F, 0F + yOff, 5F);
		STailM.setTextureSize(128, 64);
		STailM.mirror = true;
		setRotation(STailM, 1.396263F, 2.094395F, 0F);
		STailB = new ModelRenderer(this, 53, 9);
		STailB.addBox(-1F, 0F, -1F, 2, 6, 1);
		STailB.setRotationPoint(15F, 3F + yOff, -1F);
		STailB.setTextureSize(128, 64);
		STailB.mirror = true;
		setRotation(STailB, 1.396263F, 3.191625F, 0F);
		SForeL = new ModelRenderer(this, 72, 29);
		SForeL.addBox(-1F, -1F, -1F, 2, 6, 2);
		SForeL.setRotationPoint(0F, 4F + yOff, -5F);
		SForeL.setTextureSize(128, 64);
		SForeL.mirror = true;
		setRotation(SForeL, -1.570796F, -0.5235988F, 0F);
		SRearR = new ModelRenderer(this, 71, 17);
		SRearR.addBox(-1F, -1F, -1F, 2, 8, 3);
		SRearR.setRotationPoint(2F, 4F + yOff, 7F);
		SRearR.setTextureSize(128, 64);
		SRearR.mirror = true;
		setRotation(SRearR, -1.570796F + 0.5235988F, 0, 1.570796F);
		SRearL = new ModelRenderer(this, 71, 17);
		SRearL.addBox(-1F, -1F, -1F, 2, 8, 3);
		SRearL.setRotationPoint(6F, 4F + yOff, 5F);
		SRearL.setTextureSize(128, 64);
		SRearL.mirror = true;
		setRotation(SRearL, -1.570796F + 0.5235988F, 0F, 1.570796F);
		SEarR = new ModelRenderer(this, 53, 0);
		SEarR.addBox(-4F, -5F, 1F, 2, 2, 6);
		SEarR.setRotationPoint(0F, 1F + yOff, -4F);
		SEarR.setTextureSize(128, 64);
		SEarR.mirror = true;
		setRotation(SEarR, 0.5235988F, -0.5235988F, 0F);
		SEarL = new ModelRenderer(this, 53, 0);
		SEarL.addBox(2F, -5F, 1F, 2, 2, 6);
		SEarL.setRotationPoint(0F, 1F + yOff, -4F);
		SEarL.setTextureSize(128, 64);
		SEarL.mirror = true;
		setRotation(SEarL, 0.5235988F, -0.5235988F, 0F);
		SHead = new ModelRenderer(this, 83, 0);
		SHead.addBox(-3F, -4F, -8F, 6, 4, 12);
		SHead.setRotationPoint(0F, 1F + yOff, -4F);
		SHead.setTextureSize(128, 64);
		SHead.mirror = true;
		setRotation(SHead, 0.3490659F, -0.5235988F, 0F);
		SWingBoneR = new ModelRenderer(this, 0, 20);
		SWingBoneR.addBox(-2F, -1F, -1F, 16, 2, 2);
		SWingBoneR.setRotationPoint(-3F, 0F + yOff, -1F);
		SWingBoneR.setTextureSize(128, 64);
		SWingBoneR.mirror = true;
		setRotation(SWingBoneR, 0.7853982F, -1.047198F, 0F);
		SWingFlapR = new ModelRenderer(this, 0, 35);
		SWingFlapR.addBox(-3F, 1F, 0F, 17, 8, 1);
		SWingFlapR.setRotationPoint(-3F, 0F + yOff, -1F);
		SWingFlapR.setTextureSize(128, 64);
		SWingFlapR.mirror = true;
		setRotation(SWingFlapR, 0.7853982F, -1.047198F, 0F);
		SWingBoneL = new ModelRenderer(this, 0, 20);
		SWingBoneL.addBox(-2F, -1F, -1F, 16, 2, 2);
		SWingBoneL.setRotationPoint(2F, 1F + yOff, -3F);
		SWingBoneL.setTextureSize(128, 64);
		SWingBoneL.mirror = true;
		setRotation(SWingBoneL, -1.047198F, -0.1745329F, 0F);
		SWingFlapL = new ModelRenderer(this, 0, 35);
		SWingFlapL.addBox(-3F, 1F, 0F, 17, 9, 1);
		SWingFlapL.setRotationPoint(2F, 1F + yOff, -3F);
		SWingFlapL.setTextureSize(128, 64);
		SWingFlapL.mirror = true;
		setRotation(SWingFlapL, -1.134464F, -0.1745329F, 0F);

		WHead = new ModelRenderer(this, 0, 0);
		WHead.addBox(-3F, -4F, -8F, 6, 4, 12);
		WHead.setRotationPoint(0F, 1F + yOff, -4F);
		WHead.setTextureSize(128, 64);
		WHead.mirror = true;
		setRotation(WHead, 0.3490659F, -0.5235988F, 0F);

	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		if (f2 < 1500) {
			SpinesU.render(f5);
			ForeR.render(f5);
			SpinesM.render(f5);
			SpinesB.render(f5);
			Body.render(f5);
			TailM.render(f5);
			TailB.render(f5);
			ForeL.render(f5);
			RearR.render(f5);
			RearL.render(f5);
			EarR.render(f5);
			EarL.render(f5);
			Head.render(f5);
			WingBoneR.render(f5);
			WingFlapR.render(f5);
			WingBoneL.render(f5);
			WingFlapL.render(f5);
		} else {
			SSpinesU.render(f5);
			SForeR.render(f5);
			SSpinesM.render(f5);
			SSpinesB.render(f5);
			SBody.render(f5);
			STailM.render(f5);
			STailB.render(f5);
			SForeL.render(f5);
			SRearR.render(f5);
			SRearL.render(f5);

			SEarR.render(f5);
			SEarL.render(f5);
			if (f2 < 3500) {
				SHead.render(f5);
			} else {
				WHead.render(f5);
			}

			SWingBoneR.render(f5);
			SWingFlapR.render(f5);
			SWingBoneL.render(f5);
			SWingFlapL.render(f5);
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3,
			float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

		if (f2 > 100) {
			f2 -= 1000;
			int yOff = 16;
			// on the ground
			SpinesU.setRotationPoint(0F, 1F + yOff, -1F);
			SpinesU.setTextureSize(128, 64);
			setRotation(SpinesU, 1.570796F, 0F, 0F);
			ForeR.setRotationPoint(-2F, 4F + yOff, -3F);
			ForeR.setTextureSize(128, 64);
			setRotation(ForeR, 0F, 0F, 0F);
			SpinesM.setRotationPoint(0F, 1F + yOff, -1F);
			SpinesM.setTextureSize(128, 64);
			setRotation(SpinesM, 2.094395F, 0F, 0F);
			SpinesB.setRotationPoint(0F, 1F + yOff, -1F);
			SpinesB.setTextureSize(128, 64);
			setRotation(SpinesB, 1.047198F, 0F, 0F);
			Body.setRotationPoint(0F, 1F + yOff, -1F);
			Body.setTextureSize(128, 64);
			setRotation(Body, 1.570796F, 0F, 0F);
			TailM.setRotationPoint(0F, 1F + yOff, -1F);
			TailM.setTextureSize(128, 64);
			setRotation(TailM, 2.094395F, 0F, 0F);
			TailB.setRotationPoint(0F, 1F + yOff, -1F);
			TailB.setTextureSize(128, 64);
			setRotation(TailB, 1.047198F, 0F, 0F);
			ForeL.setRotationPoint(2F, 4F + yOff, -3F);
			ForeL.setTextureSize(128, 64);
			setRotation(ForeL, 0F, 0F, 0F);
			RearR.setRotationPoint(-2F, 2F + yOff, 6F);
			RearR.setTextureSize(128, 64);
			setRotation(RearR, 0F, 0F, 0F);
			RearL.setRotationPoint(2F, 2F + yOff, 6F);
			RearL.setTextureSize(128, 64);
			setRotation(RearL, 0F, 0F, 0F);
			EarR.setRotationPoint(0F, -2F + yOff, -4F);
			EarR.setTextureSize(128, 64);
			setRotation(EarR, 1.186824F, 0F, 0F);
			EarL.setRotationPoint(0F, -2F + yOff, -4F);
			EarL.setTextureSize(128, 64);
			setRotation(EarL, 1.186824F, 0F, 0F);
			Head.setRotationPoint(0F, -2F + yOff, -4F);
			Head.setTextureSize(128, 64);
			setRotation(Head, 1.186824F, 0F, 0F);
			WingBoneR.setRotationPoint(-4F, 0F + yOff, -1F);
			WingBoneR.setTextureSize(128, 64);
			setRotation(WingBoneR, -2.792527F, -1.919862F, -0.1745329F);
			WingFlapR.setRotationPoint(-4F, 0F + yOff, -1F);
			WingFlapR.setTextureSize(128, 64);
			setRotation(WingFlapR, -2.792527F, -1.919862F, -0.1745329F);
			WingBoneL.setRotationPoint(4F, 0F + yOff, -1F);
			WingBoneL.setTextureSize(128, 64);
			setRotation(WingBoneL, 2.792527F, -1.22173F, 0.1745329F);
			WingFlapL.setRotationPoint(4F, 0F + yOff, -1F);
			WingFlapL.setTextureSize(128, 64);
			setRotation(WingFlapL, 2.792527F, -1.22173F, 0.1745329F);

			setRotation(WingBoneR, -2.792527F + f2 / 20, -1.919862F,
					-0.1745329F);
			setRotation(WingFlapR, -2.792527F + f2 / 20, -1.919862F,
					-0.1745329F);
			setRotation(WingBoneL, 2.792527F + f2 / 20, -1.22173F, 0.1745329F);
			setRotation(WingFlapL, 2.792527F + f2 / 20, -1.22173F, 0.1745329F);

			this.TailM.rotateAngleY = (f2 - 0.79f) / 10;
			this.TailB.rotateAngleY = (f2 - 0.79f) / 10;
			this.SpinesU.rotateAngleY = (f2 - 0.79f) / 10;
			this.SpinesM.rotateAngleY = (f2 - 0.79f) / 10;
			this.SpinesB.rotateAngleY = (f2 - 0.79f) / 10;

			this.RearR.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
			this.RearL.rotateAngleX = MathHelper.cos(f * 0.6662F
					+ (float) Math.PI)
					* 1.4F * f1;
			this.ForeR.rotateAngleX = MathHelper.cos(f * 0.6662F
					+ (float) Math.PI)
					* 1.4F * f1;
			this.ForeL.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;

			this.Head.rotateAngleX = f4 / 57.29578F;
			this.Head.rotateAngleY = f3 / 57.29578F;
			this.Head.rotateAngleZ = 0;// f5 / 57.29578F;
			this.EarR.rotateAngleX = f4 / 57.29578F;
			this.EarR.rotateAngleY = f3 / 57.29578F;
			this.EarR.rotateAngleZ = 0;// f5 / 57.29578F;
			this.EarL.rotateAngleX = f4 / 57.29578F;
			this.EarL.rotateAngleY = f3 / 57.29578F;
			this.EarL.rotateAngleZ = 0;// f5 / 57.29578F;

		} else {
			// in the air
			SpinesU.setRotationPoint(0F, -1F, 0F);
			SpinesU.setTextureSize(128, 64);
			setRotation(SpinesU, 0F, 0F, 0F);
			ForeR.setRotationPoint(-2F, -2F, -2F);
			ForeR.setTextureSize(128, 64);
			setRotation(ForeR, -0.5235988F, 0F, 0F);
			SpinesM.setRotationPoint(0F, -1F, 0F);
			SpinesM.setTextureSize(128, 64);
			setRotation(SpinesM, 0.5235988F, 0F, 0F);
			SpinesB.setRotationPoint(0F, -1F, 0F);
			SpinesB.setTextureSize(128, 64);
			setRotation(SpinesB, -0.5235988F, 0F, 0F);
			Body.setRotationPoint(0F, -1F, 0F);
			Body.setTextureSize(128, 64);
			setRotation(Body, 0F, 0F, 0F);
			TailM.setRotationPoint(0F, -1F, 0F);
			TailM.setTextureSize(128, 64);
			setRotation(TailM, 0.5235988F, 0F, 0F);
			TailB.setRotationPoint(0F, -1F, 0F);
			TailB.setTextureSize(128, 64);
			setRotation(TailB, -0.5235988F, 0F, 0F);
			ForeL.setRotationPoint(2F, -2F, -2F);
			ForeL.setTextureSize(128, 64);
			setRotation(ForeL, -0.5235988F, 0F, 0F);
			RearR.setRotationPoint(-2F, 8F, -1F);
			RearR.setTextureSize(128, 64);
			setRotation(RearR, -0.2617994F, 0F, 0F);
			RearL.setRotationPoint(2F, 8F, -1F);
			RearL.setTextureSize(128, 64);
			setRotation(RearL, -0.2617994F, 0F, 0F);
			EarR.setRotationPoint(0F, -4F, -3F);
			EarR.setTextureSize(128, 64);
			setRotation(EarR, 0.5235988F, 0F, 0F);
			EarL.setRotationPoint(0F, -4F, -3F);
			EarL.setTextureSize(128, 64);
			setRotation(EarL, 0.5235988F, 0F, 0F);
			Head.setRotationPoint(0F, -4F, -3F);
			Head.setTextureSize(128, 64);
			setRotation(Head, 0.5235988F, 0F, 0F);
			WingBoneR.setRotationPoint(-4F, -2F, 0F);
			WingBoneR.setTextureSize(128, 64);
			setRotation(WingBoneR, -0.5235988F, -2.617994F, 0.2617994F);
			WingFlapR.setRotationPoint(-4F, -2F, 0F);
			WingFlapR.setTextureSize(128, 64);
			setRotation(WingFlapR, -0.5235988F, -2.617994F, 0.2617994F);
			WingBoneL.setRotationPoint(4F, -2F, 0F);
			WingBoneL.setTextureSize(128, 64);
			setRotation(WingBoneL, 0.5235988F, -0.5235988F, -0.2617994F);
			WingFlapL.setRotationPoint(4F, -2F, 0F);
			WingFlapL.setTextureSize(128, 64);
			setRotation(WingFlapL, 0.5235988F, -0.5235988F, -0.2617994F);

			this.WingBoneR.rotateAngleY = 3.93f - f2;
			this.WingFlapR.rotateAngleY = 3.93f - f2;
			this.WingBoneR.rotateAngleZ = 0.79f - f2 / 1.5f;
			this.WingFlapR.rotateAngleZ = 0.79f - f2 / 1.5f;
			this.WingBoneL.rotateAngleY = f2 - 0.79f;
			this.WingFlapL.rotateAngleY = f2 - 0.79f;
			this.WingBoneL.rotateAngleZ = f2 / 1.5f - 0.79f;
			this.WingFlapL.rotateAngleZ = f2 / 1.5f - 0.79f;

			this.TailM.rotateAngleZ = (f2 - 0.79f) / 5;
			this.TailB.rotateAngleZ = (f2 - 0.79f) / 5;
			this.SpinesU.rotateAngleZ = (f2 - 0.79f) / 5;
			this.SpinesM.rotateAngleZ = (f2 - 0.79f) / 5;
			this.SpinesB.rotateAngleZ = (f2 - 0.79f) / 5;

			this.Head.rotateAngleX = f4 / 57.29578F;
			this.Head.rotateAngleY = f3 / 57.29578F;
			this.Head.rotateAngleZ = 0;// f5 / 57.29578F;
			this.EarR.rotateAngleX = f4 / 57.29578F;
			this.EarR.rotateAngleY = f3 / 57.29578F;
			this.EarR.rotateAngleZ = 0;// f5 / 57.29578F;
			this.EarL.rotateAngleX = f4 / 57.29578F;
			this.EarL.rotateAngleY = f3 / 57.29578F;
			this.EarL.rotateAngleZ = 0;// f5 / 57.29578F;
		}
	}

}
