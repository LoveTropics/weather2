package weather2.attachments;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import weather2.Weather;

public class WeatherAttachments {

    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Weather.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntityNandoAttachment>> NADO_ENTITY = REGISTER.register(
            "nado_entity", () -> AttachmentType.builder(EntityNandoAttachment::new)
                    .serialize(EntityNandoAttachment.CODEC)
                    .build()
    );

}
