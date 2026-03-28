<script lang="ts">
    import ButtonSetting from "../common/setting/ButtonSetting.svelte";
    import {
        directLoginToCrackedAccount,
        getAccounts,
        loginToAccount,
        randomUsername,
        reconnectToServer
    } from "../../../integration/rest";
    import {listen} from "../../../integration/ws";
    import {onMount} from "svelte";
    import type {Account} from "../../../integration/types";
    import {restoreSession} from "../../../integration/rest.js";
    import {isLoggingIn} from "../altmanager/altmanager_store";

    const premiumAccounts: Account[] = $state([]);

    async function reconnectWithRandomUsername() {
        const username = await randomUsername();
        await directLoginToCrackedAccount(username, false);
    }

    async function reconnectWithRandomAccount() {
        const account = premiumAccounts[Math.floor(Math.random() * premiumAccounts.length)];
        await loginToAccount(account.id);
    }

    onMount(async () => {
        const accounts = await getAccounts();
        premiumAccounts.push(...accounts.filter(a => a.type !== "Cracked" && !a.favorite));
    });

    listen("accountManagerLogin", reconnectToServer);
</script>

<div class="reconnect">
    <ButtonSetting title="重新连接" on:click={reconnectToServer}
                   disabled={$isLoggingIn}/>
    <ButtonSetting title="恢复初始会话" on:click={restoreSession}
                   disabled={$isLoggingIn}/>
    <ButtonSetting title="使用随机账号重新连接" on:click={reconnectWithRandomAccount}
                   disabled={premiumAccounts.length === 0 || $isLoggingIn}/>
    <ButtonSetting title="使用随机用户名重新连接" on:click={reconnectWithRandomUsername}
                   disabled={$isLoggingIn}/>
</div>

<style lang="scss">
  .reconnect {
    position: fixed;
    bottom: 20px;
    left: 45px;
    display: flex;
    flex-direction: column;
    row-gap: 10px;
    align-items: flex-start;
  }
</style>
